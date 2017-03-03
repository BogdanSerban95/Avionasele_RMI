package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import common.IClientService;
import common.IServerService;
import common.RegistryManager;
import common.Settings;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class ClientShell extends Shell implements IClientService {
	private Text inputUserName;
	private Text inputCoordinates;
	Button btnShoot;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	IClientService clientService;
	IServerService serverService;
	private Text outputLog;

	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ClientShell shell = new ClientShell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	public ClientShell(Display display) throws MalformedURLException, RemoteException, NotBoundException {
		super(display, SWT.SHELL_TRIM);
		addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					serverService.unsubscribe(clientService);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		setLayout(new GridLayout(2, false));

		inputUserName = new Text(this, SWT.BORDER);
		inputUserName.setToolTipText("Insert your username here...");
		inputUserName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnRegister = new Button(this, SWT.NONE);
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String username = inputUserName.getText();
				if (username.isEmpty()) {
					MessageBox box = new MessageBox(getShell());
					box.setMessage("Username must not be empty!");
					box.open();
				} else {
					try {
						serverService.subscribe(clientService, username);
						btnRegister.setEnabled(false);
						inputUserName.setEditable(false);
						inputCoordinates.setEnabled(true);
						btnShoot.setEnabled(true);

					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		btnRegister.setText("Register");
		
		outputLog = new Text(this, SWT.MULTI);
		outputLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Label lblEnterShootCoordinates = new Label(this, SWT.NONE);
		lblEnterShootCoordinates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEnterShootCoordinates.setText("Target (separated by space):\r\n");
		new Label(this, SWT.NONE);

		inputCoordinates = new Text(this, SWT.BORDER);
		inputCoordinates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inputCoordinates.setEnabled(false);
		btnShoot = new Button(this, SWT.NONE);
		btnShoot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String text = inputCoordinates.getText();
				inputCoordinates.setText("");
				if (!text.isEmpty()) {
					String[] splitText = text.split(" ");
					if (splitText.length != 2) {
						MessageBox box = new MessageBox(getShell());
						box.setMessage("You must enter only 2 numbers separated by space...");
						box.open();
					} else {
						try {
							int x = Integer.parseInt(splitText[0]);
							int y = Integer.parseInt(splitText[1]);
							if (x > 0 && x <= 10 && y > 0 && y <= 10) {
								serverService.shoot(x - 1, y - 1, clientService);
							} else {
								MessageBox box = new MessageBox(getShell());
								box.setMessage("Please enter only values between 1 and 10!");
								box.open();
							}
						} catch (NumberFormatException e1) {
							MessageBox box = new MessageBox(getShell());
							box.setMessage("Please enter only two numerical values...");
							box.open();
						}catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}
		});
		btnShoot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnShoot.setText("Shoot!");
		btnShoot.setEnabled(false);
		createContents();

		this.serverService = (IServerService) Naming.lookup(MessageFormat.format("rmi://{0}:{1}/{2}",
				Settings.getServerHost(), Integer.toString(Settings.getClientPort()), Settings.getServerService()));
		Registry registry = RegistryManager.getRegistry(Settings.getClientPort());
		this.clientService = new ClientService(registry, Settings.getClientService(), this);

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(502, 304);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onReceiveMessage(String message) throws RemoteException {
		Display.getDefault().asyncExec(() -> {
			outputLog.append(message); 
			outputLog.append("\n");
		});
	}

	@Override
	public void onShotResponse(String response) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
