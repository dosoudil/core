/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.shared.subsys.web.model.VirtualServer;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class WebSubsystemView extends DisposableViewImpl implements WebPresenter.MyView{

    private WebPresenter presenter;
    private Form<JSPContainerConfiguration> form;

    private ConnectorList connectorList;
    private VirtualServerList serverList;

    @Override
    public Widget createWidget() {

        form = new Form(JSPContainerConfiguration.class);
        form.setNumColumns(2);

        FormToolStrip toolStrip = new FormToolStrip<JSPContainerConfiguration>(
                form,
                new FormToolStrip.FormCallback<JSPContainerConfiguration>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveJSPConfig(changeset);
                    }

                    @Override
                    public void onDelete(JSPContainerConfiguration entity) {

                    }
                }
        );

        toolStrip.providesDeleteOp(false);

        // ----

        CheckBoxItem disabled= new CheckBoxItem("disabled", "Disabled?");
        CheckBoxItem development= new CheckBoxItem("development", "Development?");
        TextBoxItem instanceId = new TextBoxItem("instanceId", "Instance ID", false);

        CheckBoxItem keepGenerated= new CheckBoxItem("keepGenerated", "Keep Generated?");
        NumberBoxItem checkInterval = new NumberBoxItem("checkInterval", "Check Interval");
        CheckBoxItem sourceFragment= new CheckBoxItem("displaySource", "Display Source?");


        form.setFields(disabled, development, instanceId);
        form.setFieldsInGroup(Console.CONSTANTS.common_label_advanced(), new DisclosureGroupRenderer(), keepGenerated, checkInterval, sourceFragment);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "web");
                address.add("configuration", "jsp-configuration");
                return address;
            }
        },form);

        form.setEnabled(false); // TODO:

        // ----

        connectorList = new ConnectorList(presenter);
        serverList = new VirtualServerList(presenter);

        FormLayout formlayout = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel)
                .setTools(toolStrip);

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle("Servlet")
                .setHeadline("Servlet/HTTP Configuration")
                .setDescription(Console.CONSTANTS.subsys_web_desc())
                .addDetail("Common", formlayout.build())
                .addDetail("Connectors", connectorList.asWidget())
                .addDetail("Virtual Servers", serverList.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(WebPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setConnectors(List<HttpConnector> connectors) {
        connectorList.setConnectors(connectors);
    }

    @Override
    public void enableEditConnector(boolean b) {
        connectorList.setEnabled(b);
    }

    @Override
    public void setVirtualServers(List<VirtualServer> servers) {
        serverList.setVirtualServers(servers);
    }

    @Override
    public void enableEditVirtualServer(boolean b) {
        serverList.setEnabled(b);
    }

    @Override
    public void setJSPConfig(JSPContainerConfiguration jspConfig) {
        form.edit(jspConfig);
    }

	@Override
	public void setSocketBindigs(List<String> socketBindings) {
        connectorList.setSocketBindigs(socketBindings);
		
	}

}
