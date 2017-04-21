/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.campusTour;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.TextRenderer;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Rakesh Chitturi
 */
public class FileRenderer extends TextRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {

        rendererParamsNotNull(context, component);

        if (!shouldDecode(component)) {
            return;
        }

        String clientId = decodeBehaviors(context, component);

        if (clientId == null) {
            clientId = component.getClientId(context);
        }

        assert (clientId != null);
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestMap = externalContext.getRequestParameterMap();

        if (requestMap.containsKey(clientId)) {
            setSubmittedValue(component, requestMap.get(clientId));
        }

        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        try {
            Collection<Part> parts = request.getParts();

            for (Part cur : parts) {
                if (clientId.equals(cur.getName())) {
                    component.setTransient(true);
                    setSubmittedValue(component, cur);
                }
            }
        } catch (IOException ioe) {
            throw new FacesException(ioe);
        } catch (ServletException se) {
            throw new FacesException(se);
        }
    }

    @Override

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

        if (context.isProjectStage(ProjectStage.Development)) {

            boolean produceMessage = false;

            UIForm form = RenderKitUtils.getForm(component, context);

            if (null != form) {

                String encType = (String) form.getAttributes().get("enctype");

                if (null == encType || !encType.equals("multipart/form-data")) {

                    produceMessage = true;

                }

            } else {

                produceMessage = true;

            }

            if (produceMessage) {

                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "File upload component requires a form with an enctype of multipart/form-data",
                        "File upload component requires a form with an enctype of multipart/form-data");

                context.addMessage(component.getClientId(context), message);

            }

        }

        super.encodeBegin(context, component);

    }

    @Override

    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {

        if (submittedValue instanceof Part) {

            Part part = (Part) submittedValue;

            if ((part.getHeader("content-disposition") == null || part.getHeader("content-disposition").endsWith("filename=\"\"")) && part.getSize() <= 0) {

                return null;

            }
        }
        return submittedValue;

    }
}
