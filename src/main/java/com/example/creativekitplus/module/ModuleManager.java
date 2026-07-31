package com.example.creativekitplus.module;

import com.example.creativekitplus.config.Config;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns every feature module. Instantiates them from persisted config defaults,
 * ticks the enabled ones, and exposes typed getters so keybinds can toggle a
 * specific module.
 */
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    private final FlightController flight;
    private final XrayModule xray;
    private final EntityHighlighter highlighter;
    private final AutoToolSelector autoTool;
    private final NoClipModule noClip;
    private final SpeedModifier speed;
    private final FullBright fullBright;

    public ModuleManager() {
        Config c = Config.get();
        flight      = new FlightController(c.flightEnabled);
        xray        = new XrayModule(c.xrayEnabled);
        highlighter = new EntityHighlighter(c.highlightEnabled);
        autoTool    = new AutoToolSelector(c.autoToolEnabled);
        noClip      = new NoClipModule(c.noClipEnabled);
        speed       = new SpeedModifier(c.speedEnabled, flight);
        fullBright  = new FullBright(c.fullBrightEnabled);

        modules.add(flight);
        modules.add(xray);
        modules.add(highlighter);
        modules.add(autoTool);
        modules.add(noClip);
        modules.add(speed);
        modules.add(fullBright);
    }

    public void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick(client);
            }
        }
    }

    /** Flip a module and persist the new state. Returns the new value. */
    public boolean toggle(Module m) {
        boolean next = !m.isEnabled();
        m.setEnabled(next);
        syncToConfig();
        Config.save();
        return next;
    }

    private void syncToConfig() {
        Config c = Config.get();
        c.flightEnabled     = flight.isEnabled();
        c.xrayEnabled       = xray.isEnabled();
        c.highlightEnabled  = highlighter.isEnabled();
        c.autoToolEnabled   = autoTool.isEnabled();
        c.noClipEnabled     = noClip.isEnabled();
        c.speedEnabled      = speed.isEnabled();
        c.fullBrightEnabled = fullBright.isEnabled();
    }

    public FlightController flight()      { return flight; }
    public XrayModule xray()              { return xray; }
    public EntityHighlighter highlighter(){ return highlighter; }
    public AutoToolSelector autoTool()    { return autoTool; }
    public NoClipModule noClip()          { return noClip; }
    public SpeedModifier speed()          { return speed; }
    public FullBright fullBright()        { return fullBright; }
}
