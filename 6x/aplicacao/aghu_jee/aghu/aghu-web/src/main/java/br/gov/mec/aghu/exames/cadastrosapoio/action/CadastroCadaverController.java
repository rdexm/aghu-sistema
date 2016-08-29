package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class CadastroCadaverController extends ActionController {
			
	private static final long serialVersionUID = -1691228141864387692L;

	private static final String CADASTRO_CADAVER_LIST = "cadastroCadaverList";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;

	private AelDadosCadaveres aelDadosCadaver;

	private Short convenioId;
	private Byte planoId;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if(aelDadosCadaver != null && aelDadosCadaver.getSeq() != null) {
			this.aelDadosCadaver = this.cadastrosApoioExamesFacade.obterAelDadosCadaveresPorId(aelDadosCadaver.getSeq());

			if(aelDadosCadaver == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			atribuirPlano();
		} else {
			this.aelDadosCadaver = new AelDadosCadaveres();
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			
			boolean isUpdate = aelDadosCadaver.getSeq() != null;
			this.cadastrosApoioExamesFacade.persistirAelDadosCadaveres(aelDadosCadaver);
			
			if(isUpdate) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_DADOS_CADAVER", this.aelDadosCadaver.getNome());
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_DADOS_CADAVER", this.aelDadosCadaver.getNome());
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (aelDadosCadaver != null && aelDadosCadaver.getConvenioSaudePlano() != null) {
			this.convenioId = aelDadosCadaver.getConvenioSaudePlano().getId().getCnvCodigo();
			this.planoId = aelDadosCadaver.getConvenioSaudePlano().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			aelDadosCadaver.setConvenioSaudePlano(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
	}
	
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(String param) {
		return this.internacaoFacade.pesquisarInstituicaoHospitalarPorCodigoENome(param);
	}

	public String cancelar() {
		aelDadosCadaver = null;
		convenioId = null;
		planoId = null;
		return CADASTRO_CADAVER_LIST;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public AelDadosCadaveres getAelDadosCadaver() {
		return aelDadosCadaver;
	}

	public void setAelDadosCadaver(AelDadosCadaveres aelDadosCadaver) {
		this.aelDadosCadaver = aelDadosCadaver;
	}
}