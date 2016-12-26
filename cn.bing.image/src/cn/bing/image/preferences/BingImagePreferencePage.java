package cn.bing.image.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import cn.bing.image.Activator;

public class BingImagePreferencePage extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  public BingImagePreferencePage()
  {
    super(1);
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("bing image preferences");
  }

  public void createFieldEditors()
  {
    addField(
      new DirectoryFieldEditor("path", 
      "&Directory for images to be saved :", getFieldEditorParent()));
    addField(new StringFieldEditor("url", "&Url of bing images :", getFieldEditorParent()));
  }

  public void init(IWorkbench workbench)
  {
  }
}