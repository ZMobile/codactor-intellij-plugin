package com.translator.view.uml.application;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.file.LoadRecentFileAction;
import org.jhotdraw.app.action.file.OpenRecentFileAction;
import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

public abstract class CodactorUmlBuilderAbstractApplication extends AbstractBean implements CodactorUmlBuilderApplication {
    private static final long serialVersionUID = 1L;
    private LinkedList<View> views = new LinkedList();
    private Collection<View> unmodifiableViews;
    private boolean isEnabled = true;
    protected ResourceBundleUtil labels;
    protected ApplicationModel model;
    private Preferences prefs;
    private @Nullable View activeView;
    public static final String VIEW_COUNT_PROPERTY = "viewCount";
    private LinkedList<URI> recentURIs = new LinkedList();
    private static final int maxRecentFilesCount = 10;
    private ActionMap actionMap;
    private URIChooser openChooser;
    private URIChooser saveChooser;
    private URIChooser importChooser;
    private URIChooser exportChooser;

    public CodactorUmlBuilderAbstractApplication() {
    }

    public void init() {
        this.prefs = PreferencesUtil.userNodeForPackage(this.getModel() == null ? this.getClass() : this.getModel().getClass());
        int count = this.prefs.getInt("recentFileCount", 0);

        for(int i = 0; i < count; ++i) {
            String path = this.prefs.get("recentFile." + i, (String)null);
            if (path != null) {
                try {
                    this.recentURIs.add(new URI(path));
                } catch (URISyntaxException var5) {
                }
            }
        }

    }

    public void start(List<URI> uris) {
        if (uris.isEmpty()) {
            /*final View v = this.createView();
            this.add(v);
            v.setEnabled(false);
            this.show(v);
            this.setActiveView(v);
            v.execute(new BackgroundTask() {
                public void construct() {
                    v.clear();
                }

                public void finished() {
                    v.setEnabled(true);
                }
            });*/
        } else {
            Iterator var5 = uris.iterator();

            while(var5.hasNext()) {
                final URI uri = (URI)var5.next();
                final View v = this.createView();
                this.add(v);
                v.setEnabled(false);
                show(v);
                this.setActiveView(v);
                v.execute(new BackgroundTask() {
                    public void construct() throws Exception {
                        v.read(uri, (URIChooser)null);
                    }

                    protected void done() {
                        v.setURI(uri);
                    }

                    protected void failed(Throwable error) {
                        error.printStackTrace();
                        v.clear();
                    }

                    public void finished() {
                        v.setEnabled(true);
                    }
                });
            }
        }

    }

    public View startAndReturn(List<URI> uris) {
        for (URI uri : uris) {
            if (!uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1).equals("svg")) {
                return null;
            }
        }
        if (uris.isEmpty()) {
            /*final View v = this.createView();
            this.add(v);
            v.setEnabled(false);
            this.show(v);
            this.setActiveView(v);
            v.execute(new BackgroundTask() {
                public void construct() {
                    v.clear();
                }

                public void finished() {
                    v.setEnabled(true);
                }
            });*/
        } else {
            Iterator var5 = uris.iterator();

            while(var5.hasNext()) {
                final URI uri = (URI)var5.next();
                final View v = this.createView();
                this.add(v);
                v.setEnabled(false);
                show(v);
                this.setActiveView(v);

                v.execute(new BackgroundTask() {
                    public void construct() throws Exception {
                        v.read(uri, (URIChooser)null);
                    }

                    protected void done() {
                        v.setURI(uri);
                    }

                    protected void failed(Throwable error) {
                        error.printStackTrace();
                        v.clear();
                    }

                    public void finished() {
                        v.setEnabled(true);
                    }
                });
                return v;
            }
        }
        return null;
    }

    public final View createView() {
        View v = this.basicCreateView();
        v.setActionMap(this.createViewActionMap(v));
        return v;
    }

    public void setModel(ApplicationModel newValue) {
        ApplicationModel oldValue = this.model;
        this.model = newValue;
        this.firePropertyChange("model", oldValue, newValue);
    }

    public ApplicationModel getModel() {
        return this.model;
    }

    protected View basicCreateView() {
        return this.model.createView();
    }

    public void setActiveView(@Nullable View newValue) {
        View oldValue = this.activeView;
        if (this.activeView != null) {
            this.activeView.deactivate();
        }

        this.activeView = newValue;
        if (this.activeView != null) {
            this.activeView.activate();
        }

        this.firePropertyChange("activeView", oldValue, newValue);
    }

    public @Nullable View getActiveView() {
        return this.activeView;
    }

    public String getName() {
        return this.model.getName();
    }

    public String getVersion() {
        return this.model.getVersion();
    }

    public String getCopyright() {
        return this.model.getCopyright();
    }

    public void stop() {
        Iterator var1 = (new LinkedList(this.views())).iterator();

        while(var1.hasNext()) {
            View p = (View)var1.next();
            this.dispose(p);
        }

    }

    public void destroy() {
        this.stop();
        this.model.destroyApplication(this);
        System.exit(0);
    }

    public void remove(View v) {
        this.hide(v);
        if (v == this.getActiveView()) {
            this.setActiveView((View)null);
        }

        int oldCount = this.views.size();
        this.views.remove(v);
        v.setApplication((Application)null);
        this.firePropertyChange("viewCount", oldCount, this.views.size());
    }

    public void add(View v) {
        if (v.getApplication() != this) {
            int oldCount = this.views.size();
            this.views.add(v);
            v.setApplication(this);
            v.init();
            this.model.initView(this, v);
            this.firePropertyChange("viewCount", oldCount, this.views.size());
        }

    }

    public List<View> getViews() {
        return Collections.unmodifiableList(this.views);
    }

    protected abstract ActionMap createViewActionMap(View var1);

    public void dispose(View view) {
        this.remove(view);
        this.model.destroyView(this, view);
        view.dispose();
    }

    public Collection<View> views() {
        if (this.unmodifiableViews == null) {
            this.unmodifiableViews = Collections.unmodifiableCollection(this.views);
        }

        return this.unmodifiableViews;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean newValue) {
        boolean oldValue = this.isEnabled;
        this.isEnabled = newValue;
        this.firePropertyChange("enabled", oldValue, newValue);
    }

    public Container createContainer() {
        return new JFrame();
    }

    public void launch(String[] args) {
        this.configure(args);
        final List<URI> uris = this.getOpenURIsFromMainArgs(args);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CodactorUmlBuilderAbstractApplication.this.init();
                CodactorUmlBuilderAbstractApplication.this.model.initApplication(CodactorUmlBuilderAbstractApplication.this);
                LinkedList startUris;
                if (uris.isEmpty()) {
                    startUris = new LinkedList();
                    if (CodactorUmlBuilderAbstractApplication.this.model.isOpenLastURIOnLaunch() && !CodactorUmlBuilderAbstractApplication.this.recentURIs.isEmpty()) {
                        startUris.add((URI)CodactorUmlBuilderAbstractApplication.this.recentURIs.getFirst());
                    }
                } else {
                    startUris = new LinkedList(uris);
                }

                CodactorUmlBuilderAbstractApplication.this.start(startUris);
            }
        });
    }

    protected List<URI> getOpenURIsFromMainArgs(String[] args) {
        LinkedList<URI> uris = new LinkedList();

        for(int i = 0; i < args.length; ++i) {
            if ("-open".equals(args[i])) {
                ++i;

                while(i < args.length && !args[i].startsWith("-")) {
                    URI uri = (new File(args[i])).toURI();
                    uris.add(uri);
                    ++i;
                }
            }
        }

        return uris;
    }

    protected void initLabels() {
        this.labels = ApplicationLabels.getLabels();
    }

    public void configure(String[] args) {
    }

    public void removePalette(Window palette) {
    }

    public void addPalette(Window palette) {
    }

    public void removeWindow(Window window) {
    }

    public void addWindow(Window window, @Nullable View p) {
    }

    protected Action getAction(@Nullable View view, String actionID) {
        return this.getActionMap(view).get(actionID);
    }

    protected void addAction(JMenu m, @Nullable View view, String actionID) {
        this.addAction(m, this.getAction(view, actionID));
    }

    protected void addAction(JMenu m, Action a) {
        if (a != null) {
            if (m.getClientProperty("needsSeparator") == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty("needsSeparator", (Object)null);
            }

            JMenuItem mi = m.add(a);
            mi.setIcon((Icon)null);
            mi.setToolTipText((String)null);
        }

    }

    protected void addMenuItem(JMenu m, JMenuItem mi) {
        if (mi != null) {
            if (m.getClientProperty("needsSeparator") == Boolean.TRUE) {
                m.addSeparator();
                m.putClientProperty("needsSeparator", (Object)null);
            }

            m.add(mi);
        }

    }

    protected void maybeAddSeparator(JMenu m) {
        JPopupMenu pm = m.getPopupMenu();
        if (pm.getComponentCount() > 0 && !(pm.getComponent(pm.getComponentCount() - 1) instanceof JSeparator)) {
            m.addSeparator();
        }

    }

    protected void removeTrailingSeparators(JMenu m) {
        JPopupMenu pm = m.getPopupMenu();

        for(int i = pm.getComponentCount() - 1; i > 0 && pm.getComponent(i) instanceof JSeparator; --i) {
            pm.remove(i);
        }

    }

    public List<URI> getRecentURIs() {
        return Collections.unmodifiableList(this.recentURIs);
    }

    public void clearRecentURIs() {
        List<URI> oldValue = (List)this.recentURIs.clone();
        this.recentURIs.clear();
        this.prefs.putInt("recentFileCount", this.recentURIs.size());
        this.firePropertyChange("recentURIs", Collections.unmodifiableList(oldValue), Collections.unmodifiableList(this.recentURIs));
    }

    public void addRecentURI(URI uri) {
        List<URI> oldValue = (List)this.recentURIs.clone();
        if (this.recentURIs.contains(uri)) {
            this.recentURIs.remove(uri);
        }

        this.recentURIs.addFirst(uri);
        if (this.recentURIs.size() > 10) {
            this.recentURIs.removeLast();
        }

        this.prefs.putInt("recentFileCount", this.recentURIs.size());
        int i = 0;

        for(Iterator var4 = this.recentURIs.iterator(); var4.hasNext(); ++i) {
            URI f = (URI)var4.next();
            this.prefs.put("recentFile." + i, f.toString());
        }

        this.firePropertyChange("recentURIs", oldValue, 0);
        this.firePropertyChange("recentURIs", Collections.unmodifiableList(oldValue), Collections.unmodifiableList(this.recentURIs));
    }

    protected JMenu createOpenRecentFileMenu(@Nullable View view) {
        JMenu m = new JMenu();
        this.labels.configureMenu(m, this.getAction(view, "file.load") == null && this.getAction(view, "file.loadDirectory") == null ? "file.openRecent" : "file.loadRecent");
        m.setIcon((Icon)null);
        m.add(this.getAction(view, "file.clearRecentFiles"));
        new OpenRecentMenuHandler(m, view);
        return m;
    }

    public URIChooser getOpenChooser(View v) {
        if (v == null) {
            if (this.openChooser == null) {
                this.openChooser = this.model.createOpenChooser(this, (View)null);
                this.openChooser.getComponent().putClientProperty("application", this);
                List<URI> ruris = this.getRecentURIs();
                if (ruris.size() > 0) {
                    try {
                        this.openChooser.setSelectedURI((URI)ruris.get(0));
                    } catch (IllegalArgumentException var5) {
                    }
                }
            }

            return this.openChooser;
        } else {
            URIChooser chooser = (URIChooser)v.getComponent().getClientProperty("openChooser");
            if (chooser == null) {
                chooser = this.model.createOpenChooser(this, v);
                v.getComponent().putClientProperty("openChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty("application", this);
                List<URI> ruris = this.getRecentURIs();
                if (ruris.size() > 0) {
                    try {
                        chooser.setSelectedURI((URI)ruris.get(0));
                    } catch (IllegalArgumentException var6) {
                    }
                }
            }

            return chooser;
        }
    }

    public URIChooser getSaveChooser(View v) {
        if (v == null) {
            if (this.saveChooser == null) {
                this.saveChooser = this.model.createSaveChooser(this, (View)null);
                this.saveChooser.getComponent().putClientProperty("application", this);
            }

            return this.saveChooser;
        } else {
            URIChooser chooser = (URIChooser)v.getComponent().getClientProperty("saveChooser");
            if (chooser == null) {
                chooser = this.model.createSaveChooser(this, v);
                v.getComponent().putClientProperty("saveChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty("application", this);

                try {
                    chooser.setSelectedURI(v.getURI());
                } catch (IllegalArgumentException var4) {
                }
            }

            return chooser;
        }
    }

    public URIChooser getImportChooser(View v) {
        if (v == null) {
            if (this.importChooser == null) {
                this.importChooser = this.model.createImportChooser(this, (View)null);
                this.importChooser.getComponent().putClientProperty("application", this);
            }

            return this.importChooser;
        } else {
            URIChooser chooser = (URIChooser)v.getComponent().getClientProperty("importChooser");
            if (chooser == null) {
                chooser = this.model.createImportChooser(this, v);
                v.getComponent().putClientProperty("importChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty("application", this);
            }

            return chooser;
        }
    }

    public URIChooser getExportChooser(View v) {
        if (v == null) {
            if (this.exportChooser == null) {
                this.exportChooser = this.model.createExportChooser(this, (View)null);
                this.exportChooser.getComponent().putClientProperty("application", this);
            }

            return this.exportChooser;
        } else {
            URIChooser chooser = (URIChooser)v.getComponent().getClientProperty("exportChooser");
            if (chooser == null) {
                chooser = this.model.createExportChooser(this, v);
                v.getComponent().putClientProperty("exportChooser", chooser);
                chooser.getComponent().putClientProperty("view", v);
                chooser.getComponent().putClientProperty("application", this);
            }

            return chooser;
        }
    }

    public void setActionMap(ActionMap m) {
        this.actionMap = m;
    }

    public ActionMap getActionMap(@Nullable View v) {
        return v == null ? this.actionMap : v.getActionMap();
    }

    private class OpenRecentMenuHandler implements PropertyChangeListener, Disposable {
        private JMenu openRecentMenu;
        private LinkedList<Action> openRecentActions = new LinkedList();
        private @Nullable View view;

        public OpenRecentMenuHandler(@Nullable JMenu openRecentMenu, View view) {
            this.openRecentMenu = openRecentMenu;
            this.view = view;
            if (view != null) {
                view.addDisposable(this);
            }

            this.updateOpenRecentMenu();
            CodactorUmlBuilderAbstractApplication.this.addPropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == "recentURIs") {
                this.updateOpenRecentMenu();
            }

        }

        protected void updateOpenRecentMenu() {
            if (this.openRecentMenu.getItemCount() > 0) {
                JMenuItem clearRecentFilesItem = this.openRecentMenu.getItem(this.openRecentMenu.getItemCount() - 1);
                this.openRecentMenu.remove(this.openRecentMenu.getItemCount() - 1);
                Iterator var2 = this.openRecentActions.iterator();

                while(var2.hasNext()) {
                    Action actionx = (Action)var2.next();
                    if (actionx instanceof Disposable) {
                        ((Disposable)actionx).dispose();
                    }
                }

                this.openRecentActions.clear();
                this.openRecentMenu.removeAll();
                URI f;
                if (CodactorUmlBuilderAbstractApplication.this.getAction(this.view, "file.load") == null && CodactorUmlBuilderAbstractApplication.this.getAction(this.view, "file.loadDirectory") == null) {
                    var2 = CodactorUmlBuilderAbstractApplication.this.getRecentURIs().iterator();

                    while(var2.hasNext()) {
                        f = (URI)var2.next();
                        OpenRecentFileAction action = new OpenRecentFileAction(CodactorUmlBuilderAbstractApplication.this, f);
                        this.openRecentMenu.add(action);
                        this.openRecentActions.add(action);
                    }
                } else {
                    var2 = CodactorUmlBuilderAbstractApplication.this.getRecentURIs().iterator();

                    while(var2.hasNext()) {
                        f = (URI)var2.next();
                        LoadRecentFileAction actionxx = new LoadRecentFileAction(CodactorUmlBuilderAbstractApplication.this, this.view, f);
                        this.openRecentMenu.add(actionxx);
                        this.openRecentActions.add(actionxx);
                    }
                }

                if (CodactorUmlBuilderAbstractApplication.this.getRecentURIs().size() > 0) {
                    this.openRecentMenu.addSeparator();
                }

                this.openRecentMenu.add(clearRecentFilesItem);
            }

        }

        public void dispose() {
            CodactorUmlBuilderAbstractApplication.this.removePropertyChangeListener(this);
            Iterator var1 = this.openRecentActions.iterator();

            while(var1.hasNext()) {
                Action action = (Action)var1.next();
                if (action instanceof Disposable) {
                    ((Disposable)action).dispose();
                }
            }

            this.openRecentActions.clear();
        }
    }
}