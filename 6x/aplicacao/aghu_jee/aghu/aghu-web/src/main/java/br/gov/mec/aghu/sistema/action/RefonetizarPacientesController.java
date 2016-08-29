package br.gov.mec.aghu.sistema.action;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class RefonetizarPacientesController extends ActionController {

	private static final Log LOG = LogFactory.getLog(RefonetizarPacientesController.class);

	private static final long serialVersionUID = -1443814938858252297L;

	@EJB 
	private IPacienteFacade pacienteFacade;
	
	private Boolean novosUsuarios = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String refonetizarPacientes() {
		
		File file = this.pacienteFacade.refonetizarPacientes(novosUsuarios);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType("application/x-zip-compressed");
		response.addHeader("Content-disposition", "attachment; filename=scripts.zip");

		try {
			ServletOutputStream os = response.getOutputStream();

			if (file != null) {
				os.write(FileUtils.readFileToByteArray(file));
				file.delete();
			}
			
			os.flush();
			os.close();
			facesContext.responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "Ocorreu um erro ao gerar o script.");
		}
		return null;
	}

	public Boolean getNovosUsuarios() {
		return novosUsuarios;
	}

	public void setNovosUsuarios(Boolean novosUsuarios) {
		this.novosUsuarios = novosUsuarios;
	}
}