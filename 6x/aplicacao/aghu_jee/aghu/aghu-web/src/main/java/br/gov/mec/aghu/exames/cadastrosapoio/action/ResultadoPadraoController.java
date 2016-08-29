package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ResultadoPadraoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -1221282977588252360L;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IExamesFacade examesFacade;

	private AelResultadoPadraoCampo resultadoPadraoCampo = new AelResultadoPadraoCampo();

	// suggestion titulo
	private AelResultadosPadrao resultadoPadrao;
	// suggestion resultado codificado
	private AelResultadoCodificado resultadoCodificado;
	// suggestion resultado caracteristica
	private AelResultadoCaracteristica resultadoCaracteristica;

	private AelDescricoesResulPadrao descResultaPadrao = new AelDescricoesResulPadrao();

	private Integer seqParam;
	private String voltarPara; // O padrão é voltar para interface de pesquisa

	/**
	 * operacao a ser realizada
	 */
	private DominioOperacaoBanco operacao;

	public void iniciar() {
	 


		this.resultadoPadraoCampo = this.cadastrosApoioExamesFacade.obterResultadoPadraoCampoPorParametro(this.seqParam, null, null);

		if (this.resultadoPadraoCampo == null) {
			this.resultadoPadraoCampo = new AelResultadoPadraoCampo();
			this.resultadoPadraoCampo.setCampoLaudo(this.examesFacade.obterCampoLaudoPorSeq(this.seqParam));
			this.operacao = DominioOperacaoBanco.INS;
		} else {
			this.descResultaPadrao = this.cadastrosApoioExamesFacade.obterAelDescricoesResulPadrao(this.resultadoPadraoCampo.getId().getRpaSeq(), this.resultadoPadraoCampo.getId().getSeqp());
			this.resultadoPadrao = this.resultadoPadraoCampo.getResultadoPadrao();

			if (this.descResultaPadrao == null) {
				this.descResultaPadrao = new AelDescricoesResulPadrao();
			}

			if (DominioTipoCampoCampoLaudo.C == this.resultadoPadraoCampo.getCampoLaudo().getTipoCampo()) {
				if (this.resultadoPadraoCampo.getResultadoCodificado() != null) {
					this.resultadoCodificado = this.resultadoPadraoCampo.getResultadoCodificado();
				} else {
					this.resultadoCaracteristica = this.resultadoPadraoCampo.getResultadoCaracteristica();
				}
			}

			this.operacao = DominioOperacaoBanco.UPD;
		}
	
	}

	public void gravar() {

		try {

			if (DominioTipoCampoCampoLaudo.C == this.resultadoPadraoCampo.getCampoLaudo().getTipoCampo()) {
				this.resultadoPadraoCampo.setResultadoCodificado(this.resultadoCodificado);
				this.resultadoPadraoCampo.setResultadoCaracteristica(this.resultadoCaracteristica);
			}

			if (DominioOperacaoBanco.INS == this.operacao) {
				this.resultadoPadraoCampo.setResultadoPadrao(this.resultadoPadrao);
				this.cadastrosApoioExamesFacade.persistirAelResultadoPadraoCampo(this.resultadoPadraoCampo);

				// qdo for tipo alfanumerico
				if (DominioTipoCampoCampoLaudo.A == this.resultadoPadraoCampo.getCampoLaudo().getTipoCampo()) {
					this.descResultaPadrao.setResultadoPadraoCampo(this.resultadoPadraoCampo);
					this.cadastrosApoioExamesFacade.persistirAelDescricoesResulPadrao(descResultaPadrao);
				}

				this.operacao = DominioOperacaoBanco.UPD;

				this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_SALVAR_RESUL_PADRAO_CAMPO");
			} else {
				this.cadastrosApoioExamesFacade.atualizarAelResultadoPadraoCampo(this.resultadoPadraoCampo);
				// qdo for tipo alfanumerico
				if (DominioTipoCampoCampoLaudo.A == this.resultadoPadraoCampo.getCampoLaudo().getTipoCampo()) {
					if (descResultaPadrao != null && descResultaPadrao.getId() == null) {
						this.descResultaPadrao.setResultadoPadraoCampo(this.resultadoPadraoCampo);
					}
					this.cadastrosApoioExamesFacade.atualizarAelDescricoesResulPadrao(this.descResultaPadrao);
				}
				this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_ATUALIZAR_RESUL_PADRAO_CAMPO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {

		String retorno = this.voltarPara;

		this.resultadoPadraoCampo = new AelResultadoPadraoCampo();
		this.resultadoPadrao = null;
		this.resultadoCodificado = null;
		this.resultadoCaracteristica = null;
		this.descResultaPadrao = new AelDescricoesResulPadrao();
		this.seqParam = null;
		this.voltarPara = null;
		this.operacao = null;

		return retorno;
	}

	public String excluir() {
		try {
			// qdo for tipo alfanumerico
			if (DominioTipoCampoCampoLaudo.A == this.resultadoPadraoCampo.getCampoLaudo().getTipoCampo()) {
				final AelDescricoesResulPadrao descResulPadrao = this.cadastrosApoioExamesFacade.obterAelDescricoesResulPadrao(this.resultadoPadraoCampo.getId().getRpaSeq(), this.resultadoPadraoCampo
						.getId().getSeqp());

				this.cadastrosApoioExamesFacade.removerAelDescricoesResulPadrao(descResulPadrao);
			}

			this.cadastrosApoioExamesFacade.removerAelResultadoPadraoCampo(this.resultadoPadraoCampo);

			this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_REMOVER_RESUL_PADRAO_CAMPO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return this.voltar();
	}

	public List<AelResultadosPadrao> sbObterTitulo(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.sbListarResultadosPadrao((String) objPesquisa);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<AelResultadosPadrao>();
		}
	}

	public List<AelResultadoCodificado> sbObterResultadoCodificado(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.sbListarResultadoCodificado((String) objPesquisa, this.resultadoPadraoCampo.getCampoLaudo().getSeq());
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<AelResultadoCodificado>();
		}
	}

	public List<AelResultadoCaracteristica> sbObterResultadoCaracteristica(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.listarResultadoCaracteristica((String) objPesquisa, this.resultadoPadraoCampo.getCampoLaudo().getSeq());
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<AelResultadoCaracteristica>();
		}
	}

	/** GET/SET **/

	public AelResultadosPadrao getResultadoPadrao() {
		return resultadoPadrao;
	}

	public AelResultadoCodificado getResultadoCodificado() {
		return resultadoCodificado;
	}

	public void setResultadoPadrao(AelResultadosPadrao resultadoPadrao) {
		this.resultadoPadrao = resultadoPadrao;
	}

	public void setResultadoCodificado(AelResultadoCodificado resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}

	public AelResultadoCaracteristica getResultadoCaracteristica() {
		return resultadoCaracteristica;
	}

	public void setResultadoCaracteristica(AelResultadoCaracteristica resultadoCaracteristica) {
		this.resultadoCaracteristica = resultadoCaracteristica;
	}

	public Integer getSeqParam() {
		return seqParam;
	}

	public void setSeqParam(Integer seqParam) {
		this.seqParam = seqParam;
	}

	public DominioOperacaoBanco getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacaoBanco operacao) {
		this.operacao = operacao;
	}

	public AelResultadoPadraoCampo getResultadoPadraoCampo() {
		return resultadoPadraoCampo;
	}

	public void setResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) {
		this.resultadoPadraoCampo = resultadoPadraoCampo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelDescricoesResulPadrao getDescResultaPadrao() {
		return descResultaPadrao;
	}

	public void setDescResultaPadrao(AelDescricoesResulPadrao descResultaPadrao) {
		this.descResultaPadrao = descResultaPadrao;
	}

}
