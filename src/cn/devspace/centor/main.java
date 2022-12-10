package cn.devspace.centor;

import cn.devspace.nucleus.Plugin.PluginBase;

public class main extends PluginBase {
    @Override
    public void onLoad() {
        super.onLoad();
        sendLog("Centorsome启动中...");
        sendLog(translateMessage("Test","Centorsome"));
    }
}
