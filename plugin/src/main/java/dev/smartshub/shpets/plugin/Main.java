package dev.smartshub.shpets.plugin;

import dev.smartshub.shpets.api.PetsAPI;
import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.api.registry.PetInstanceRegistry;
import dev.smartshub.shpets.api.registry.PetTemplateRegistry;
import dev.smartshub.shpets.api.service.boost.RivalBoostService;
import dev.smartshub.shpets.plugin.command.PetCommand;
import dev.smartshub.shpets.plugin.command.handler.exception.ExceptionHandler;
import dev.smartshub.shpets.plugin.command.handler.parameter.PetParameterType;
import dev.smartshub.shpets.plugin.listener.PetGlowListener;
import dev.smartshub.shpets.plugin.listener.PlayerJoinListener;
import dev.smartshub.shpets.plugin.listener.PlayerQuitListener;
import dev.smartshub.shpets.plugin.listener.RivalBoostListener;
import dev.smartshub.shpets.plugin.message.MessageParser;
import dev.smartshub.shpets.plugin.message.MessageRepository;
import dev.smartshub.shpets.plugin.packet.PacketHandlerImpl;
import dev.smartshub.shpets.plugin.pet.factory.PetFactory;
import dev.smartshub.shpets.plugin.pet.registry.NMSEntityRegistry;
import dev.smartshub.shpets.plugin.service.particle.ParticleHandlingService;
import dev.smartshub.shpets.plugin.service.placeholder.PlaceholderHandlingService;
import dev.smartshub.shpets.plugin.service.scheduler.PaperTaskScheduler;
import dev.smartshub.shpets.plugin.service.skull.SkullHandlingService;
import dev.smartshub.shpets.plugin.service.config.ConfigService;
import dev.smartshub.shpets.plugin.service.glow.GlowEffectService;
import dev.smartshub.shpets.plugin.service.glow.GlowHandlingService;
import dev.smartshub.shpets.plugin.service.notify.NotifyService;
import dev.smartshub.shpets.plugin.service.pet.PetService;
import dev.smartshub.shpets.plugin.service.pet.PetTemplateLoaderService;
import dev.smartshub.shpets.plugin.task.AsyncJobTask;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public class Main extends JavaPlugin {

    private static Main instance;

    private PetTemplateRegistry templateRegistry;
    private PetInstanceRegistry instanceRegistry;
    private NMSEntityRegistry nmsEntityRegistry;

    private MessageParser messageParser;
    private MessageRepository messageRepository;

    private PetFactory petFactory;

    private ConfigService configService;
    private NotifyService notifyService;
    private PetService petService;
    private PetTemplateLoaderService petTemplateLoaderService;
    private GlowEffectService glowEffectService;
    private GlowHandlingService glowHandlingService;

    private AsyncJobTask petTicker;

    private PacketHandlerImpl packetHandler;

    @Override
    public void onEnable() {
        instance = this;

        initRegistries();
        setUpConfig();
        initializeServices();
        startPetTicker();
        subscribePacketHandler();

        registerListener();
        registerCommands();
        initAPI();

        demo();
    }

    private void initRegistries() {
        templateRegistry = new PetTemplateRegistry();
        instanceRegistry = new PetInstanceRegistry();
        nmsEntityRegistry = new NMSEntityRegistry();
    }

    private void setUpConfig() {
        configService = new ConfigService(this);
        messageParser = new MessageParser();
        messageRepository = new MessageRepository(configService);
    }

    private void initializeServices() {
        RivalBoostService.getInstance().initialize(this);
        petFactory = new PetFactory();
        petService = new PetService(templateRegistry, instanceRegistry, petFactory, messageParser);
        petTemplateLoaderService = new PetTemplateLoaderService(this, petService);

        notifyService = new NotifyService(messageParser, messageRepository);

        // Load pet templates from configuration
        petTemplateLoaderService.loadAllTemplates();

        glowEffectService = new GlowEffectService();
        glowHandlingService = new GlowHandlingService(this, glowEffectService);
    }

    private void startPetTicker() {
        petTicker = new AsyncJobTask(petService);
        petTicker.runTaskTimerAsynchronously(this, 20L, 1L);
    }

    private void subscribePacketHandler() {
        packetHandler = new PacketHandlerImpl(petService);
    }

    private void registerListener() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(packetHandler), this);
        pm.registerEvents(new PlayerQuitListener(packetHandler), this);
        pm.registerEvents(new PetGlowListener(glowHandlingService), this);
        pm.registerEvents(new RivalBoostListener(), this);
    }

    private void registerCommands() {
        final var exceptionHandler = new ExceptionHandler(notifyService);

        var lamp = BukkitLamp.builder(this)
                .parameterTypes(builder -> builder.addParameterType(PetTemplate.class, new PetParameterType(templateRegistry)))
                .exceptionHandler(exceptionHandler)
                .build();

        lamp.register(new PetCommand(petService, this));
    }

    private void initAPI() {
        PetsAPI.setInstance(new PetsAPI(
                PlaceholderHandlingService.INSTANCE,
                new SkullHandlingService(this),
                new GlowHandlingService(this, glowEffectService),
                new ParticleHandlingService(),
                new PaperTaskScheduler(this),
                templateRegistry,
                instanceRegistry,
                nmsEntityRegistry
        ));
    }

    private void demo() {

        Bukkit.getScheduler().runTaskLater(this, () -> {
            System.out.println("[SHPets] DEMO VERSION - Plugin will be disabled in 1 hour.");
        }, 20L);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            System.out.println("[SHPets] Plugin will be disabled in 30m due to is the DEMO version.");
        }, 36000L);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.getPluginManager().disablePlugin(this);
            System.out.println("[SHPets] Disabled plugin. Version for demonstration purposes.");
        }, 72000L);
    }

    @Override
    public void onDisable() {
        // Despawn all pets
        instanceRegistry.getAllSpawned().forEach(Pet::despawn);
        instanceRegistry.clear();
        RivalBoostService.getInstance().shutdown();
    }

    public static Main getInstance() {
        return instance;
    }

    public static void runAsync(Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), runnable, delay, period);
    }

    public static void runSync(Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), runnable, delay, period);
    }

}
