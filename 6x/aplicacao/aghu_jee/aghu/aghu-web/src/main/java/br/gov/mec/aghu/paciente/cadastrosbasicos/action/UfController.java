package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class UfController extends ActionController {

	private static final long serialVersionUID = 7271962574471861730L;

	private static final Log LOG = LogFactory.getLog(UfController.class);

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private static final String UF_LIST = "ufList";
	
	private AipUfs aipUf = new AipUfs();

	private String aipSiglaUF;

	@PostConstruct
	protected void init() {
		this.begin(conversation);
		aipUf = new AipUfs();
	}
	
	public List<AipPaises> pesquisarPaisesPorDescricao(String strPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarPaisesPorDescricao((String) strPesquisa);
	}

	public String confirmar() {

		try {

			if (StringUtils.isBlank(aipSiglaUF)) {
				this.cadastrosBasicosPacienteFacade.salvarUF(this.aipUf);
			} else {
				this.cadastrosBasicosPacienteFacade.alterarUF(this.aipUf);
			}

			if (StringUtils.isBlank(this.aipSiglaUF)) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_UF", this.aipUf.getNome());
				
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_UF", this.aipUf.getNome());
			}
			
			return cancelar();
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de UFs
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		aipSiglaUF = null;
		aipUf = new AipUfs();
		return UF_LIST;
	}

	public void limparPais() {
		if (this.aipUf != null && this.aipUf.getPais() != null) {
			this.aipUf.setPais(null);
		}
	}

	public boolean isMostrarLinkExcluirPais() {
		return (this.aipUf != null && this.aipUf.getPais() != null);
	}

	// ### GETs e SETs ###

	public AipUfs getAipUf() {
		return aipUf;
	}

	public void setAipUf(AipUfs aipUf) {
		this.aipUf = aipUf;
	}

	public String getAipSiglaUF() {
		return aipSiglaUF;
	}

	public void setAipSiglaUF(String aipSiglaUF) {
		this.aipSiglaUF = aipSiglaUF;
	}

	public void setPais(AipPaises _pais) {
		this.aipUf.setPais(_pais);
	}
}
