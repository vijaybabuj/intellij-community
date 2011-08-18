/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.ui;

import com.intellij.execution.ExecutionBundle;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.GuiUtils;
import com.intellij.ui.InsertPathAction;
import com.intellij.ui.TextFieldWithHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: anna
 * Date: Jun 21, 2005
 */
public class AlternativeJREPanel extends JPanel {
  private final ComponentWithBrowseButton<TextFieldWithHistory> myPathField;
  private final JCheckBox myCbEnabled;
  final TextFieldWithHistory myFieldWithHistory;

  public AlternativeJREPanel() {
    super(new GridBagLayout());

    myCbEnabled = new JCheckBox(ExecutionBundle.message("run.configuration.use.alternate.jre.checkbox"));
    myCbEnabled.setPreferredSize(new Dimension(154, 16));
    final GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                                                         GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
    add(myCbEnabled, gc);

    myFieldWithHistory = new TextFieldWithHistory();
    final ArrayList<String> foundJDKs = new ArrayList<String>();
    final Sdk[] allJDKs = ProjectJdkTable.getInstance().getAllJdks();
    for (Sdk jdk : allJDKs) {
      foundJDKs.add(jdk.getHomePath());
    }
    myFieldWithHistory.setHistory(foundJDKs);
    myPathField = new ComponentWithBrowseButton<TextFieldWithHistory>(myFieldWithHistory, null);
    myPathField.addBrowseFolderListener(ExecutionBundle.message("run.configuration.select.alternate.jre.label"),
                                        ExecutionBundle.message("run.configuration.select.jre.dir.label"),
                                        null, BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR,
                                        TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);
    gc.gridx = 1;
    gc.weightx = 1;
    add(myPathField, gc);
    InsertPathAction.addTo(myFieldWithHistory.getTextEditor());

    gc.weighty = 1;
    add(Box.createVerticalBox(), gc);

    myCbEnabled.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        enabledChanged();
      }
    });
    enabledChanged();
  }

  private void enabledChanged() {
    final boolean pathEnabled = isPathEnabled();
    GuiUtils.enableChildren(myPathField, pathEnabled);
    myFieldWithHistory.invalidate(); //need to revalidate inner component
  }

  public String getPath() {
    return FileUtil.toSystemIndependentName(myPathField.getChildComponent().getText().trim());
  }

  private void setPath(final String path) {
    myPathField.getChildComponent().setText(FileUtil.toSystemDependentName(path == null ? "" : path));
  }

  public boolean isPathEnabled() {
    return myCbEnabled.isSelected();
  }

  private void setPathEnabled(boolean b) {
    myCbEnabled.setSelected(b);
    enabledChanged();
  }

  public void init(String path, boolean isEnabled){
    setPathEnabled(isEnabled);
    setPath(path);
  }
}
