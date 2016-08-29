package br.gov.mec.aghu.sicon.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

// TODO: Auto-generated Javadoc
/**
 * The Class ManterRescicaoContratoController.
 */


public class ManterRescicaoContratoController extends ActionController {

	private static final long serialVersionUID = 3122132909465900135L;
	
	private static final String PAGE_GERENCIAR_CONTRATOS = "sicon-gerenciarContratos";
	
	private static final String PAGE_MANTER_RESCISAO_CONTRATOS = "manterRescisaoContrato";

	/** The sicon facade. */
	@EJB
	private ISiconFacade siconFacade;

	/** The sco contrato. */
	//@EJB
	private ScoContrato scoContrato;
	
	private String contSeq;
	

	/** The res contrato. */
	private ScoResContrato resContrato;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	/**
	 * Inits the.
	 */
	public void init(){
	 

		try {
			if (getContSeq() == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_PARAMETRO_CONTRATO_NULO");
			} else {
				scoContrato = siconFacade.getContrato(Integer
						.valueOf(getContSeq()));
				this.resContrato = siconFacade
						.getRescicaoContrato(this.scoContrato);
			}
			if (resContrato == null) {
				resContrato = new ScoResContrato();
				resContrato.setIndSituacao(DominioSituacaoEnvioContrato.A);
				resContrato.setContrato(scoContrato);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	/**
	 * Gravar rescicao.
	 *
	 * @return the string
	 */
	public String gravarRescicao(){
		try {
			
			boolean isUpdate = this.resContrato.getSeq()==null ? false : true;
			this.resContrato.setObservacoes(StringUtils.strip(this.resContrato.getObservacoes()));
			this.resContrato.setJustificativa(StringUtils.strip(this.resContrato.getJustificativa()));
			siconFacade.inserirAtualizarRescicao(this.resContrato);
			if(!isUpdate) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_RESC_CONTRATO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ATUALIZACAO_RESC_CONTRATO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_GERENCIAR_CONTRATOS;
	}
	
	/**
	 * Limpar.
	 *
	 * @return the string
	 */
	public String limpar(){
		return PAGE_MANTER_RESCISAO_CONTRATOS;
	}

	/**
	 * Gets the cpf cnpj.
	 *
	 * @return the cpf cnpj
	 */
	public String getCpfCnpj(){
		if(this.scoContrato!=null && this.scoContrato.getFornecedor()!=null && this.scoContrato.getFornecedor().getCgc()!=null) {
			return  CoreUtil.formatarCNPJ(this.scoContrato.getFornecedor().getCgc());
		} else if(this.scoContrato!=null && this.scoContrato.getFornecedor()!=null && this.scoContrato.getFornecedor().getCpf()!=null) {
			return CoreUtil.formataCPF(this.scoContrato.getFornecedor().getCpf());
		}
		return "";
	}
	
	/**
	 * Voltar.
	 *
	 * @return the string
	 */
	public String voltar(){
		return PAGE_GERENCIAR_CONTRATOS;
	}

	/**
	 * Gets the res contrato.
	 *
	 * @return the res contrato
	 */
	public ScoResContrato getResContrato() {
		return resContrato;
	}

	/**
	 * Sets the res contrato.
	 *
	 * @param resContrato the new res contrato
	 */
	public void setResContrato(ScoResContrato resContrato) {
		this.resContrato = resContrato;
	}

	/**
	 * Gets the sco contrato.
	 *
	 * @return the sco contrato
	 */
	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	/**
	 * Sets the sco contrato.
	 *
	 * @param scoContrato the new sco contrato
	 */
	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}
	
	public String getContSeq() {
		return contSeq;
	}

	public void setContSeq(String contSeq) {
		this.contSeq = contSeq;
	}

	
}
