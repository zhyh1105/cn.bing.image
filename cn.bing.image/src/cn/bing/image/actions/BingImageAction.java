package cn.bing.image.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cn.bing.image.Activator;

public class BingImageAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private boolean isRunning = false;

	public void run(IAction action) {
		if (this.isRunning) {
			return;
		}
		this.isRunning = true;
		try {
			Job job = new Job("loading images from http://cn.bing.com ... ") {
				protected IStatus run(IProgressMonitor monitor) {
					BingImageAction.this.getImages(monitor);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.isRunning = false;
		}
	}

	private void getImages(IProgressMonitor monitor) {
		monitor.beginTask("", 20);
		String b = "download image";
		String e = "...";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		try {
			HttpClient httpClient = new HttpClient();
			final File dir = new File(store.getString("path"));
			if ((!dir.exists()) || (!dir.isDirectory())) {
				dir.mkdir();
			}
			HttpMethod get = new GetMethod();
			String u = store.getString("url");
			int i = 20;
			for (int j = 1; i > 0; j++) {
				get.setPath(u + (i - 2));
				httpClient.executeMethod(get);
				String content = get.getResponseBodyAsString().trim();
				if (!"null".equals(content)) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode root = mapper.readTree(content);
					JsonNode images = root.path("images");
					JsonNode image = images.get(0);
					String url = image.path("url").getTextValue();
					url = new URL(store.getString("url")).toURI().resolve(url).toString();
					String date = image.path("startdate").getTextValue();
					monitor.setTaskName(b + date + e);
					String formatName = FilenameUtils.getExtension(url);
					File dest = new File(dir, date + "." + formatName);
					if (!dest.exists()) {
						ImageIO.write(ImageIO.read(new URL(url)), formatName, dest);
					}
					mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
					File desc = new File(dir, date + ".txt");
					if (!desc.exists()) {
						FileOutputStream fos = new FileOutputStream(desc);
						fos.write(mapper.writeValueAsString(root).getBytes());
						fos.close();
					}
				}
				monitor.worked(j);
				i--;
			}

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (MessageDialog.openConfirm(BingImageAction.this.window.getShell(), "Download successfully",
							"Open the target dir ?")) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.OPEN))
							try {
								desktop.open(dir);
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
				}
			});
		} catch (final Exception ex) {
			ex.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(BingImageAction.this.window.getShell(), "Error occured", ex.getMessage());
				}
			});
		}
		monitor.done();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}