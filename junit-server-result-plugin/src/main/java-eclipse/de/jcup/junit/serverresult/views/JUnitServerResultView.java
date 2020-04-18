/*
 * Copyright 2020 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.junit.serverresult.views;

import static de.jcup.junit.serverresult.JUnitServerResultConsoleUtil.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.junit.serverresult.Activator;
import de.jcup.junit.serverresult.JUnitServerResultCanvas;
import de.jcup.junit.serverresult.JUnitServerResultColorConstants;
import de.jcup.junit.serverresult.JUnitServerResultConstants;
import de.jcup.junit.serverresult.JUnitServerResultUtil;
import de.jcup.junit.serverresult.JUnitTestSuite;
import de.jcup.junit.serverresult.JunitImportContext;
import de.jcup.junit.serverresult.JunitModel;
import de.jcup.junit.serverresult.JunitModel.JUnitModelStatistics;
import de.jcup.junit.serverresult.JunitXMLExporter;
import de.jcup.junit.serverresult.LogFileLocation;
import de.jcup.junit.serverresult.PatternNameMatcher;
import de.jcup.junit.serverresult.TimeStampArea;
import de.jcup.junit.serverresult.TimeStampAreaFinder;
import de.jcup.junit.serverresult.importWizards.JunitResultImportWizard;
import de.jcup.junit.serverresult.preferences.JUnitServerResultPreferences;

public class JUnitServerResultView extends ViewPart {

    private static final String DRAG_JUNIT_XML_FILE_S_OR_A_FOLDER_TO_IMPORT_RESULTS = "Drag junit xml file(s) or a folder to import results";
    public static final String ID = "de.jcup.junit.serverresult.views.JUnitServerResultView";
    private TimeStampAreaFinder timeStampAreaFinder = new TimeStampAreaFinder();
    private Image imgJunitFailure;
    private Image imgJunitError;
    private Image imgJunitOk;
    private ImageDescriptor imgDescImportWizard;
    private ImageDescriptor imgDescShowFailuresOnly;
    private ImageDescriptor imgDescLinkWithLogFile;
    private ImageDescriptor imgDescShowSkippedOnly;

    private TableViewer viewer;
    private Action removeAllAction;
    private Action removeSelectedAction;
    private Action showOnlyFailuresToggleAction;
    private Action doubleClickAction;
    private DragAndDropSupport dragAndDropSupport = new DragAndDropSupport();
    private JUnitResultFileImportJobStarter importJobStarter = new JUnitResultFileImportJobStarter();
    private JunitXMLExporter exporter = new JunitXMLExporter();

    private boolean showFailuresOnly;
    private boolean showSkippedOnly;
    private boolean linkedWithLogFile;

    private Action linkWithLogFileAction;

    private Text logFileText;
    private Text testSuiteNameFilterText;
    private Action showOnlySkippedToggleAction;
    private Action importResultsAction;
    private JUnitServerResultCanvas serverResultCanvas;
    private Color colorSucces;
    private Color colorError;
    private Color colorFailures;
    private Color colorSkipped;

    @Override
    public void createPartControl(Composite parent) {
        imgDescShowFailuresOnly = createImageDescriptor("failures.png");
        imgDescShowSkippedOnly = createImageDescriptor("testignored.png");
        imgDescLinkWithLogFile = createImageDescriptor("synced.png");
        imgDescImportWizard = createImageDescriptor("import_wiz.png");

        imgJunitFailure = createImage("tsuitefail.png");
        imgJunitError = createImage("tsuiteerror.png");
        imgJunitOk = createImage("tsuiteok.png");
        
        colorError=getColor(JUnitServerResultColorConstants.COLOR_JUNIT_RED);
        colorFailures=getColor(JUnitServerResultColorConstants.COLOR_JUNIT_RED);
        colorSkipped=getColor(JUnitServerResultColorConstants.COLOR_JUNIT_BLUE);
        colorSucces=getColor(JUnitServerResultColorConstants.COLOR_JUNIT_GREEN);

        Composite composite = new Composite(parent, SWT.FILL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;

        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite testSuiteFilterComposite = new Composite(composite, SWT.BORDER);
        testSuiteFilterComposite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        GridLayout testSuiteCompositeLayout = new GridLayout();
        testSuiteCompositeLayout.numColumns = 2;

        testSuiteNameFilterText = new Text(testSuiteFilterComposite,  SWT.SINGLE | SWT.BORDER | SWT.SEARCH
                | SWT.ICON_CANCEL);
        testSuiteNameFilterText.setText("");
        testSuiteNameFilterText.setMessage("type filter text");
        testSuiteNameFilterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        testSuiteNameFilterText.setToolTipText("Filter testsuites by name. Wildcard (*) is supported");
        testSuiteNameFilterText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));//COLOR_LIST_BACKGROUND));

        testSuiteNameFilterText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                viewer.refresh();

            }
        });
        serverResultCanvas = new JUnitServerResultCanvas(testSuiteFilterComposite);
        GridData layout2=new GridData(SWT.FILL, SWT.CENTER, true, false);
        layout2.minimumHeight=15;
        layout2.heightHint=20;
        serverResultCanvas.setLayoutData(layout2);
        
        serverResultCanvas.setColorSuccess(colorSucces);
        serverResultCanvas.setColorError(colorError);
        serverResultCanvas.setColorFailure(colorFailures);
        serverResultCanvas.setColorSkipped(colorSkipped);
        
        testSuiteFilterComposite.setLayout(testSuiteCompositeLayout);
        testSuiteFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        
        
        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL) {
            protected void inputChanged(Object input, Object oldInput) {
                super.inputChanged(input, oldInput);

                JunitImportContext context = JUnitServerResultUtil.getContext();
                JunitModel model = context.getModelOrNull();
                if (serverResultCanvas==null) {
                    return;
                }
                if (model == null || model.testSuites.isEmpty()) {
                    serverResultCanvas.setStatistics(null);
                    viewer.getTable().setToolTipText(DRAG_JUNIT_XML_FILE_S_OR_A_FOLDER_TO_IMPORT_RESULTS);
                } else {
                    JUnitModelStatistics stat = model.createStatistics();
                    serverResultCanvas.setStatistics(stat);
                    viewer.getTable().setToolTipText("Doubleclick to show results in JUnit view");
                }

            }
        };
        viewer.setFilters(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (!(element instanceof JUnitTestSuite)) {
                    return false;
                }
                JUnitTestSuite ts = (JUnitTestSuite) element;
                /* filter by name */
                String filterText = testSuiteNameFilterText.getText();

                boolean showMe = true;

                showMe = showMe && checkNamePatternMatches(ts, filterText);
                showMe = showMe && checkShowfailuresOnly(ts);
                showMe = showMe && checkShowSkippedOnly(ts);

                return showMe;
            }

            private boolean checkNamePatternMatches(JUnitTestSuite ts, String filterText) {
                /* filter name */
                if (!PatternNameMatcher.patternMatchesName(filterText, ts.name)) {
                    return false;
                }
                return true;
            }

            private boolean checkShowSkippedOnly(JUnitTestSuite ts) {
                /* filter skipped only */
                if (!showSkippedOnly) {
                    return true;
                }
                if (ts.hasSkipped()) {
                    return true;
                }
                return false;
            }

            private boolean checkShowfailuresOnly(JUnitTestSuite ts) {
                /* filter failures only */
                if (!showFailuresOnly) {
                    return true;
                }
                if (ts.hasFailures() || ts.hasErrors()) {
                    return true;
                }
                return false;
            }
        });
        viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTable().setToolTipText(DRAG_JUNIT_XML_FILE_S_OR_A_FOLDER_TO_IMPORT_RESULTS);

        viewer.setContentProvider(ArrayContentProvider.getInstance());
        
        JunitImportContext context = JUnitServerResultUtil.getContext();
        JunitModel model = context.getModelOrNull();

        viewer.setInput(model);
        viewer.setLabelProvider(new ViewLabelProvider());

        // logfile
        Composite logFileComposite = new Group(composite, SWT.SHADOW_NONE);
        GridLayout logFileLayout = new GridLayout();
        logFileLayout.numColumns = 2;

        Label logFileLabel = new Label(logFileComposite, SWT.NONE);
        logFileLabel.setText("Log file:");

        logFileText = new Text(logFileComposite, SWT.NONE);
        String logFileHint = "Here you can drag/define a log file";
        logFileText.setMessage(logFileHint);
        logFileText.setToolTipText(logFileHint + ".\nWhen linking is enabled a double click on entry will also try to resolve log specific part of double clicked testsuite entry");
        logFileText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        logFileComposite.setLayout(testSuiteCompositeLayout);
        logFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        dragAndDropSupport.enableDragAndDrop(logFileText, new DragAndDropRunnable() {

            @Override
            public void run(DragAndDropContext context) {
                if (context.targetFiles.isEmpty()) {
                    return;
                }
                logFileText.setText(context.targetFiles.get(0).getAbsolutePath());
                autoEnableLinkWithLogFileWhenWanted();

            }

        });
        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "de.jcup.junit.serverresult.viewer");
        getSite().setSelectionProvider(viewer);

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        dragAndDropSupport.enableDragAndDrop(viewer.getTable(), new DragAndDropRunnable() {

            @Override
            public void run(DragAndDropContext context) {
                removeAllTestSuites();
                importJobStarter.startImport(context.targetFiles);
            }

        });
    }

    private Color getColor(RGB rgb) {
        return Activator.getDefault().getColorManager().getColor(rgb);
    }

    private void removeAllTestSuites() {
        JunitImportContext context = JUnitServerResultUtil.getContext();
        context.setModel(null);
        viewer.getTable().setToolTipText(DRAG_JUNIT_XML_FILE_S_OR_A_FOLDER_TO_IMPORT_RESULTS);
    }

    protected void autoEnableLinkWithLogFileWhenWanted() {
        if (!JUnitServerResultPreferences.getInstance().getAutoLinkLogFileWhenLogFileLocationChanges()) {
            return;
        }
        linkedWithLogFile = true;
        linkWithLogFileAction.setChecked(linkedWithLogFile);
    }

    private Image createImage(String imageName) {
        return createImageDescriptor(imageName).createImage();
    }

    private ImageDescriptor createImageDescriptor(String imageName) {
        return EclipseUtil.createImageDescriptor("/icons/" + imageName, Activator.PLUGIN_ID);
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                JUnitServerResultView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    @Override
    public void dispose() {
        if (imgJunitFailure != null) {
            imgJunitFailure.dispose();
        }
        if (imgJunitError != null) {
            imgJunitError.dispose();
        }
        if (imgJunitOk != null) {
            imgJunitOk.dispose();
        }
        super.dispose();
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(removeSelectedAction);
        manager.add(removeAllAction);
        manager.add(new Separator());
        manager.add(showOnlyFailuresToggleAction);
        manager.add(showOnlySkippedToggleAction);
        manager.add(new Separator());
        manager.add(linkWithLogFileAction);
        manager.add(importResultsAction);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(removeSelectedAction);
        manager.add(removeAllAction);
        manager.add(new Separator());
        manager.add(showOnlyFailuresToggleAction);
        manager.add(showOnlySkippedToggleAction);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(linkWithLogFileAction);
        manager.add(importResultsAction);
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(removeSelectedAction);
        manager.add(showOnlyFailuresToggleAction);
        manager.add(showOnlySkippedToggleAction);
        manager.add(linkWithLogFileAction);
        manager.add(importResultsAction);
    }

    private void makeActions() {
        removeSelectedAction = new Action() {
            @Override
            public void run() {
                ISelection selection = viewer.getSelection();
                if (selection == null) {
                    return;
                }
                if (!(selection instanceof IStructuredSelection)) {
                    return;
                }
                JunitImportContext context = JUnitServerResultUtil.getContext();
                JunitModel model = context.getModelOrNull();
                if (model == null) {
                    return;
                }

                IStructuredSelection str = (IStructuredSelection) selection;
                Iterator<?> iterator = str.iterator();
                while (iterator.hasNext()) {
                    Object obj = iterator.next();
                    if (!(obj instanceof JUnitTestSuite)) {
                        continue;
                    }
                    JUnitTestSuite testsuite = (JUnitTestSuite) obj;
                    model.testSuites.remove(testsuite.name);
                    serverResultCanvas.setStatistics(model.createStatistics());
                }
                viewer.refresh();
            }
        };
        removeSelectedAction.setText("Remove selected");
        removeSelectedAction.setToolTipText("Remove selected testsuite elements");
        removeSelectedAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

        removeAllAction = new Action() {
            public void run() {
                removeAllTestSuites();
            }
        };
        removeAllAction.setText("Remove all");
        removeAllAction.setToolTipText("Remove all testsuite elements");
        removeAllAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));

        showOnlyFailuresToggleAction = new Action() {
            public void run() {
                showFailuresOnly = !showFailuresOnly;
                setChecked(showFailuresOnly);
                viewer.refresh();
            }
        };

        showOnlyFailuresToggleAction.setChecked(showFailuresOnly);
        showOnlyFailuresToggleAction.setText("Show failures only");
        showOnlyFailuresToggleAction.setToolTipText("Show failed testsuites only");
        showOnlyFailuresToggleAction.setImageDescriptor(imgDescShowFailuresOnly);

        showOnlySkippedToggleAction = new Action() {
            public void run() {
                showSkippedOnly = !showSkippedOnly;
                setChecked(showSkippedOnly);
                viewer.refresh();
            }
        };
        showOnlySkippedToggleAction.setChecked(showFailuresOnly);
        showOnlySkippedToggleAction.setText("Show skipped only");
        showOnlySkippedToggleAction.setToolTipText("Show testsuites containing skippped tests only");
        showOnlySkippedToggleAction.setImageDescriptor(imgDescShowSkippedOnly);

        linkWithLogFileAction = new Action() {

            public void run() {
            	boolean toggleAllowed=true;
            	if(!linkedWithLogFile) {
            		if (logFileText==null ) {
            			toggleAllowed=false;
            		}else {
            			String text = logFileText.getText();
            			if (text==null || text.isEmpty()) {
            				MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "No logfile defined", "Please define logfile to link with!\n(See bottom of view)");
            				toggleAllowed=false;
            			}
            		}
            	}
            	if (toggleAllowed) {
            		linkedWithLogFile = !linkedWithLogFile;
            	}
                setChecked(linkedWithLogFile);
            }
        };
        linkWithLogFileAction.setChecked(linkedWithLogFile);
        linkWithLogFileAction.setText("Link with logfile");
        linkWithLogFileAction.setToolTipText("Link with server logfile - double click on testsuite will fetch same time period from log and show in editor");
        linkWithLogFileAction.setImageDescriptor(imgDescLinkWithLogFile);

        doubleClickAction = new Action() {
            public void run() {
                IStructuredSelection selection = viewer.getStructuredSelection();
                Object obj = selection.getFirstElement();
                if (obj instanceof JUnitTestSuite) {
                    JUnitTestSuite ts = (JUnitTestSuite) obj;

                    showJUnitTestResultsOfSuiteInJunitView(ts);

                    showTestOutputResultsInConsole(ts);

                    if (!linkedWithLogFile) {
                        return;
                    }
                    SimpleDateFormat logFileFormat = new SimpleDateFormat(JUnitServerResultPreferences.getInstance().getLogTimeDatePattern());
                    Path path = Paths.get(logFileText.getText());
                    showRelevantLogPartsInEditor(ts, path, logFileFormat);
                }
            }

        };
        importResultsAction = new Action("Import JUnit results", imgDescImportWizard) {
            @Override
            public void run() {
                Shell activeShell = EclipseUtil.getActiveWorkbenchShell();

                JunitResultImportWizard wizard = new JunitResultImportWizard();
                wizard.init(PlatformUI.getWorkbench(), null);

                WizardDialog dialog = new WizardDialog(activeShell, wizard);

                dialog.open();
            }
        };

    }

    private void showJUnitTestResultsOfSuiteInJunitView(JUnitTestSuite ts) {
        Job job1 = new Job("Show server results in junit view") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    File targetFile = Files.createTempFile("junit_serverresult_plugin", ".xml").toFile();
                    exporter.export(ts, targetFile);
                    JUnitCore.importTestRunSession(targetFile);
                    JUnitServerResultUtil.showTestRunnerViewPartInActivePage();

                    targetFile.delete();

                    return Status.OK_STATUS;
                } catch (IOException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Junit view import failed", e);
                } catch (CoreException e) {
                    return e.getStatus();
                }
            }
        };
        job1.schedule();
    }

    private void showTestOutputResultsInConsole(JUnitTestSuite ts) {
        Job job2 = new Job("Show server output in console") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                String output = ts.systemOut.toString();
                String error = ts.systemErr.toString();
                String prefix = ">>> Testsuite '" + ts.name + "' ";

                clearConsole();
                showConsole();
                if (!output.isEmpty()) {
                    output(prefix + "System.out:");
                    output(output);
                }

                if (!error.isEmpty()) {
                    output(prefix + "System.err:");
                    output(error);
                }

                if (output.isEmpty() && error.isEmpty()) {
                    /* both empty - so give hint: */
                    output(prefix + "did not make any output to System.out or System.err");
                }

                return Status.OK_STATUS;
            }
        };
        job2.schedule();
    }

    private void showRelevantLogPartsInEditor(JUnitTestSuite ts, Path path, SimpleDateFormat logFileFormat) {
        if (!(path.toFile().isFile() && path.toFile().exists())) {
            throw new IllegalStateException(
                    "Please define a server log file at bottom of JUnit server result view! \n\nWhen you want to link the testsuite with server log output, you must define an existing server log file!");
        }
        TimeStampArea wantedArea = timeStampAreaFinder.findArea(ts);
        final StringBuilder targetTextBuilder = new StringBuilder();
        targetTextBuilder.append("Origin logfile: " + path.toAbsolutePath().toString());
        targetTextBuilder.append("\nTruncated:\n");
        targetTextBuilder.append(">>> Begin:" + JUnitServerResultConstants.EDITOR_TIMESTAMP_FORMAT.format(wantedArea.beginTimeStamp));
        targetTextBuilder.append("\n");
        targetTextBuilder.append(">>> End  :" + JUnitServerResultConstants.EDITOR_TIMESTAMP_FORMAT.format(wantedArea.endTimeStamp));
        targetTextBuilder.append("\n\nFor testsuite:").append(ts.name).append("\n - Timestamp:").append(ts.timeStamp).append("\n - Time:").append(ts.timeInSeconds).append(" seconds");
        targetTextBuilder.append("\n\n");

        JUnitServerResultUtil.setLogEditorText(path, targetTextBuilder.toString() + "Please wait, log file is being inspected for given time period ...");

        Job job3 = new Job("Inspect log file parts related with junit server result") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {

                    ProgressMonitorCallbackAdapter adapter = new ProgressMonitorCallbackAdapter(monitor,
                            () -> JUnitServerResultUtil.setLogEditorText(path, targetTextBuilder.toString() + "Log file inspection canceled by user"));
                    byte[] bytes = Files.readAllBytes(path);
                    String text = new String(bytes, StandardCharsets.UTF_8);

                    /* long running operation: */
                    LogFileLocation location = timeStampAreaFinder.findLogFileLocationInArea(text, logFileFormat, wantedArea, adapter);

                    if (adapter.isCanceledRequested()) {
                        return Status.CANCEL_STATUS;
                    }

                    if (location.begin == -1) {
                        targetTextBuilder.append("Given time period was not found inside logs");
                    } else {
                        if (location.end == -1) {
                            targetTextBuilder.append(text.substring(location.begin));
                        } else {
                            targetTextBuilder.append(text.substring(location.begin, location.end));
                        }
                    }
                    String targetText = targetTextBuilder.toString();
                    JUnitServerResultUtil.setLogEditorText(path, targetText);
                    return Status.OK_STATUS;
                } catch (IOException e) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Junit view import failed", e);
                }
            }

        };
        job3.schedule();
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            if (obj instanceof JUnitTestSuite) {
                JUnitTestSuite suite = (JUnitTestSuite) obj;
                return suite.name;
            }
            return getText(obj);
        }

        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        @Override
        public Image getImage(Object obj) {
            if (obj instanceof JUnitTestSuite) {
                JUnitTestSuite suite = (JUnitTestSuite) obj;
                if (suite.hasErrors()) {
                    return imgJunitError;
                }
                if (suite.hasFailures()) {
                    return imgJunitFailure;
                }
                return imgJunitOk;
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    public StructuredViewer getViewer() {
        return viewer;
    }

}
