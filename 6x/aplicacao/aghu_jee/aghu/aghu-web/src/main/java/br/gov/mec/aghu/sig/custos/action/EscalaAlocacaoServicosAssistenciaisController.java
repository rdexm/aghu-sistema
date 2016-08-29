package br.gov.mec.aghu.sig.custos.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtividade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigEscalaPessoa;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EscalaAlocacaoServicosAssistenciaisController extends ActionController {

	private static final String ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_LIST = "escalaAlocacaoServicosAssistenciaisList";

	private static final Log LOG = LogFactory.getLog(EscalaAlocacaoServicosAssistenciaisController.class);

	private static final long serialVersionUID = -8630170010697191885L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private DominioTipoAtividade tipoAtividade;
	private Integer percentual;
	private SigEscalaPessoa sigEscalaPessoas;
	private Integer seqEscalaPessoas;
	private FccCentroCustos fccCentroCustos;
	private boolean emEdicao;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 


		if (this.seqEscalaPessoas != null) {
			this.setEmEdicao(Boolean.TRUE);
			this.sigEscalaPessoas = this.custosSigCadastrosBasicosFacade
					.obterEscalaPessoas(this.seqEscalaPessoas);
			this.tipoAtividade = this.sigEscalaPessoas.getTipoAtividade();
			this.percentual = this.sigEscalaPessoas.getPercentual();
			this.fccCentroCustos = this.sigEscalaPessoas.getFccCentroCustos();
		}else{
			this.limpar();
		}
	
	}

	public void gravar() {

		RapServidores servidorLogado;
		Date dataOriginalDaEscala = new Date();
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			if(this.sigEscalaPessoas.getCriadoEm() != null){
				dataOriginalDaEscala = this.sigEscalaPessoas.getCriadoEm();
			}
			this.sigEscalaPessoas.setCriadoEm(new Date());
			this.sigEscalaPessoas.setFccCentroCustos(this.fccCentroCustos);
			this.sigEscalaPessoas.setPercentual(this.percentual);
			this.sigEscalaPessoas.setServidor(servidorLogado);
			this.sigEscalaPessoas.setTipoAtividade(this.tipoAtividade);
			this.verificaPercentualMaiorInformadoNaEscala(fccCentroCustos, sigEscalaPessoas);
		
		if (this.emEdicao) {
			this.sigEscalaPessoas.setCriadoEm(dataOriginalDaEscala);
			this.custosSigCadastrosBasicosFacade.editar(this.sigEscalaPessoas);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ALTERACAO_CENTRO_CUSTO_SUCESSO",
					this.fccCentroCustos.getNomeReduzido());

		} else {
			if (this.custosSigCadastrosBasicosFacade
					.verificaExistenciaDaAtividadeNoCentroDeCusto(sigEscalaPessoas) != null) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_CENTRO_CUSTO_X_ATIVIDADE_JA_EXISTE",
						this.fccCentroCustos.getNomeReduzido(),
						this.tipoAtividade.getDescricao());
			} else {
				this.custosSigCadastrosBasicosFacade.gravar(sigEscalaPessoas);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_ESCALA_ASSISTENCIAL");
			}
		}
		
		if (!this.verificaPercentualInformadoNaEscala(fccCentroCustos)) {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_PERCENTUAL_NAO_FECHOU_TOTAL_PERCENTUAL",
					this.fccCentroCustos.getNomeReduzido());
		}
		this.limpar();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa)
			throws BaseException {
		return centroCustoFacade.pesquisarCentroCustoComStatusDaEspecialidade(
				paramPesquisa, DominioSituacao.A);
	}

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
	}

	public void limpar() {
		this.setTipoAtividade(null);
		this.setPercentual(null);
		this.setSigEscalaPessoas(new SigEscalaPessoa());
		this.setSeqEscalaPessoas(null);
		this.setFccCentroCustos(null);
		this.setEmEdicao(Boolean.FALSE);
	}

	private boolean verificaPercentualInformadoNaEscala(
			FccCentroCustos centroCustos) {
		return this.custosSigCadastrosBasicosFacade
				.pesquisarEscalaPessoasCentroCusto(centroCustos);
	}
	
	private void verificaPercentualMaiorInformadoNaEscala(
			FccCentroCustos centroCustos, SigEscalaPessoa escala) throws ApplicationBusinessException{
			this.custosSigCadastrosBasicosFacade.verificarEscalaPessoasCentroCusto(centroCustos, escala);	
	}

	public String voltar() {
		this.limpar();
		return ESCALA_ALOCACAO_SERVICOS_ASSISTENCIAIS_LIST;
	}

	public DominioTipoAtividade getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(DominioTipoAtividade tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	public Integer getPercentual() {
		return percentual;
	}

	public void setPercentual(Integer percentual) {
		this.percentual = percentual;
	}

	public SigEscalaPessoa getSigEscalaPessoas() {
		return sigEscalaPessoas;
	}

	public void setSigEscalaPessoas(SigEscalaPessoa sigEscalaPessoas) {
		this.sigEscalaPessoas = sigEscalaPessoas;
	}

	public Integer getSeqEscalaPessoas() {
		return seqEscalaPessoas;
	}

	public void setSeqEscalaPessoas(Integer seqEscalaPessoas) {
		this.seqEscalaPessoas = seqEscalaPessoas;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

}
