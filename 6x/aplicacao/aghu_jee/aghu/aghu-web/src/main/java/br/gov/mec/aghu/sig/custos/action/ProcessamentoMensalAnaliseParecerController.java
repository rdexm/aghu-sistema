package br.gov.mec.aghu.sig.custos.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoAnalises;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProcessamentoMensalAnaliseParecerController extends ActionController {

	private static final String PESQUISAR_ANALISE_OBJETOS_CUSTO = "pesquisarAnaliseObjetosCusto";

	private static final long serialVersionUID = -8346981833568307417L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer seqProcessamentoCusto;
	private Integer seqProcessamentoAnalise;
	private Long cxtSeq;

	private Integer codigoCentroCusto;

	private SigProcessamentoCusto processamentoCusto;
	private SigProcessamentoAnalises processamentoAnalise;

	private List<VisualizarAlertasProcessamentoVO> listaAlertas;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.processamentoCusto = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.seqProcessamentoCusto);
		this.processamentoAnalise = this.custosSigProcessamentoFacade.obterProcessamentoAnalise(this.seqProcessamentoAnalise);
		this.codigoCentroCusto = this.processamentoAnalise.getFccCentroCustos().getCodigo();
		this.listaAlertas = this.custosSigProcessamentoFacade.buscarAlertasPorProcessamentoCentroCusto(this.seqProcessamentoCusto, this.codigoCentroCusto);
	
	}

	public void gravar() {
		try {
			RapServidores rapServidores = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			this.processamentoAnalise.setDtParecer(new Date());
			this.processamentoAnalise.setRapServidores(rapServidores);
			this.custosSigProcessamentoFacade.atualizarProcessamentoAnalise(this.processamentoAnalise);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARECER_ANALISE_SUCESSO", this.processamentoCusto.getCompetenciaMesAno(),
					this.processamentoAnalise.getFccCentroCustos().getNomeReduzido());

			this.centralPendenciaFacade.excluirPendencia(this.getCxtSeq());

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String consultar() {
		return PESQUISAR_ANALISE_OBJETOS_CUSTO;
	}

	// getters and setters
	public SigProcessamentoCusto getProcessamentoCusto() {
		return processamentoCusto;
	}

	public void setProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.processamentoCusto = processamentoCusto;
	}

	public Integer getSeqProcessamentoCusto() {
		return seqProcessamentoCusto;
	}

	public void setSeqProcessamentoCusto(Integer seqProcessamentoCusto) {
		this.seqProcessamentoCusto = seqProcessamentoCusto;
	}

	public SigProcessamentoAnalises getProcessamentoAnalise() {
		return processamentoAnalise;
	}

	public void setProcessamentoAnalise(SigProcessamentoAnalises processamentoAnalise) {
		this.processamentoAnalise = processamentoAnalise;
	}

	public Integer getSeqProcessamentoAnalise() {
		return seqProcessamentoAnalise;
	}

	public void setSeqProcessamentoAnalise(Integer seqProcessamentoAnalise) {
		this.seqProcessamentoAnalise = seqProcessamentoAnalise;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public Long getCxtSeq() {
		return cxtSeq;
	}

	public void setCxtSeq(Long cxtSeq) {
		this.cxtSeq = cxtSeq;
	}

	public List<VisualizarAlertasProcessamentoVO> getListaAlertas() {
		return listaAlertas;
	}

	public void setListaAlertas(List<VisualizarAlertasProcessamentoVO> listaAlertas) {
		this.listaAlertas = listaAlertas;
	}
}
