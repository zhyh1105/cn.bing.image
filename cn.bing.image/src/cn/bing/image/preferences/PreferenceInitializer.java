package cn.bing.image.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import cn.bing.image.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
  public void initializeDefaultPreferences()
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault("path", "D:/media/bing");
    store.setDefault("url", "http://cn.bing.com/HPImageArchive.aspx?format=js&n=1&idx=");
  }
}