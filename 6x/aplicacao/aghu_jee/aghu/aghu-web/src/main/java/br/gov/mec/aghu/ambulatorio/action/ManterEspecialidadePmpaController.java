package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacEspecialidadePmpa;
import br.gov.mec.aghu.model.AacEspecialidadePmpaId;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;

public class ManterEspecialidadePmpaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ManterEspecialidadePmpaController.class);

    private static final long serialVersionUID = -8228628339254143000L;

    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @EJB
    private IAghuFacade aghuFacade;

    private Short seq;
    private Long codigo;
    private DominioOperacoesJournal operacao;
    private AacEspecialidadePmpa aacEspecialidadePmpa;
    private AacEspecialidadePmpaId idAacEspecialidadePmpa;
    private AghEspecialidades especialidade;
    
    private final String REDIRECIONA_MANTER_ESPECIALIDADE_PMPA_LIST = "ambulatorio-manterEspecialidadePMPAList";
    
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
    public void inicio() {
	 

	 

		this.especialidade = null;
		this.seq = null;
		this.codigo = null;
    
	}
	
    
    public String confirmar() {
	
		this.aacEspecialidadePmpa = new AacEspecialidadePmpa();
		this.idAacEspecialidadePmpa = new AacEspecialidadePmpaId();
		this.aacEspecialidadePmpa.setId(idAacEspecialidadePmpa);
	
		try {
		    this.aacEspecialidadePmpa.getId().setEspSeq(especialidade.getSeq());
		    this.aacEspecialidadePmpa.getId().setCodigo(codigo);
		    this.aacEspecialidadePmpa.setAghEspecialidades(especialidade);
		    this.ambulatorioFacade.persistirEspecialidadePmpa(aacEspecialidadePmpa);
		    this.getBundle().getString("MSG_ESPECIALIDADE_INCLUIDO_SUCESSO");
		    
		} catch (BaseException e) {
		    apresentarExcecaoNegocio(e);
		    return null;
		} catch (PersistenceException e) {
		    LOG.error("Excecao capturada", e);
		    if (e.getCause() instanceof ConstraintViolationException
			    || e.getCause() instanceof NonUniqueObjectException) {
		    	
		    	this.getBundle().getString("MSG_ESPECIALIDADE_EXISTENTE");
		    } else {
		    	this.getBundle().getString("MSG_ESPECIALIDADE_INCLUIDO_ERRO");
		    }
		    return null;
		}
		return REDIRECIONA_MANTER_ESPECIALIDADE_PMPA_LIST;
    }

	public String cancelar() {
		LOG.info("Cancelado");
		return REDIRECIONA_MANTER_ESPECIALIDADE_PMPA_LIST;
	}

    public List<AghEspecialidades> listarEspecialidades(String objPesquisa) {
    	return aghuFacade.pesquisarEspecialidadePorNomeOuSigla(objPesquisa != null ? objPesquisa : null);
    }

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public AacEspecialidadePmpa getAacEspecialidadePmpa() {
		return aacEspecialidadePmpa;
	}

	public void setAacEspecialidadePmpa(
			AacEspecialidadePmpa aacEspecialidadePmpa) {
		this.aacEspecialidadePmpa = aacEspecialidadePmpa;
	}

	public AacEspecialidadePmpaId getIdAacEspecialidadePmpa() {
		return idAacEspecialidadePmpa;
	}

	public void setIdAacEspecialidadePmpa(AacEspecialidadePmpaId idAacEspecialidadePmpa) {
		this.idAacEspecialidadePmpa = idAacEspecialidadePmpa;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

}
