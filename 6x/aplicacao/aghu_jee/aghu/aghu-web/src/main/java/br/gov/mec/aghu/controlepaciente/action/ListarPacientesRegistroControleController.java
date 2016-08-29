package br.gov.mec.aghu.controlepaciente.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ListarPacientesRegistroControleController extends ActionController{


	private static final Log LOG = LogFactory.getLog(ListarPacientesRegistroControleController.class);
	
	private static final String CONFIGURAR_LISTA = "controlepaciente-configurarLista";
	
	private static final long serialVersionUID = -557645485332817056L;

	@EJB
	IControlePacienteFacade controlePacienteFacade;
	
	@EJB
	IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaService;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	List<PacienteInternadoVO> lista = new ArrayList<PacienteInternadoVO>();
	private Integer atdSeq;
	private Integer pacCodigo;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 
	
		try {
			this.lista = this.controlePacienteFacade.pesquisarPacientesInternados( servidorLogadoFacade.obterServidorLogado());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			return;
		}
		if (this.lista.isEmpty()) {
			apresentarMsgNegocio(Severity.WARN,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO");
		}
	
	}
	

	public String configurarLista(){
		return CONFIGURAR_LISTA;
	}
	
	public String realizarChamada(String idTela) throws ApplicationBusinessException {
		if (StringUtils.equalsIgnoreCase(idTela, "controlepaciente-manterRegistros")) {
			AghAtendimentos atendimento = aghuFacade.obterAtendimentoPeloSeq(this.atdSeq);
			if (!controlePacienteFacade.validaUnidadeFuncionalInformatizada(atendimento, null)) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CONTROLE_PACIENTE_NAO_INFORMATIZADO");
				return null;
			} 	
			boolean podeAcessar = this.temPermissaoParaManterRegistros();
			if (!podeAcessar) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ACESSAR_MANTER_CONTROLES");
				return null;
			}
		}
		return idTela;
	}
	
	/**
	 * Retorna true se a pessoa fornecida possui permissão para acessar a página de manter controles.
	 */
	public boolean temPermissaoParaManterRegistros()  {
		return cascaService.usuarioTemPermissao(obterLoginUsuarioLogado(), "/controlepaciente/manterControlesPaciente.xhtml", "render");
	}
	
	public List<PacienteInternadoVO> getLista() {
		return lista;
	}

	public void setLista(List<PacienteInternadoVO> lista) {
		this.lista = lista;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}