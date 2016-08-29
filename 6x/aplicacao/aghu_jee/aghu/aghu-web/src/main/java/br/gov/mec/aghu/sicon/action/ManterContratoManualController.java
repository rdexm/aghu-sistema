package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioFixoDemanda;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioReceitaDespesa;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoGarantia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoLicitacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterContratoManualController extends ActionController {


	private static final long serialVersionUID = -467493398889139404L;
	
	private static final String PAGE_MANTER_ITENS_CONTRATO = "manterItensContrato";
	private static final String PAGE_MANTER_AF_CONTRATO = "manterAfContrato";
	
	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;


	private ScoContrato contratoManual;

	private Long nrContrato;

	private boolean edicao;
	private boolean uasgRespLicitacaoObrigatorio;
	private String periodoDescritivo;
	private boolean incisoObrigatorio;
	private boolean contratoEnviado;
	private Integer numLicitacao;
	private boolean modalidadeObrigatoria;
	private List<ScoItensContrato> listaItensContrato;
	private ContratoLicitacaoVO af;
	private List<ContratoLicitacaoVO> afs;
	private boolean valorAlterado;
	private boolean confirmarVolta;
	private boolean tipoGarantiaInexistente;
	private boolean satisfazRn02;
	private boolean bSituacaoDoContrato;

	private String voltarParaUrl;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private void situacaoDoContratoEhEnviado() {
		if (contratoManual != null
				&& (contratoManual.getSituacao().equals(DominioSituacaoEnvioContrato.E) || contratoManual.getSituacao().equals(
						DominioSituacaoEnvioContrato.AR))) {
			bSituacaoDoContrato = true;
		} else {
			bSituacaoDoContrato = false;
		}
	}

	private void validaRn02() {
		if (contratoManual != null && bSituacaoDoContrato && !contratoManual.getAditivos().isEmpty()) {
			satisfazRn02 = true;
		} else {
			satisfazRn02 = false;
		}
	}

	public void iniciar() {
		if (nrContrato != null) {
			contratoManual = siconFacade.obterContratoPorNumeroContrato(nrContrato);

			if (contratoManual.getIndOrigem() != DominioOrigemContrato.A) {
				preparaAlteracaoContratoManual();
			} else {
				preparaAlteracaoContratoAuto();
			}
		} else {
			preparaInclusaoContratoManual();
		}
		confirmarVolta = false;
		setValorAlterado(false);
		ajustaValorGarantia();
		situacaoDoContratoEhEnviado();
		validaRn02();		
	}

	private void preparaAlteracaoContratoAuto() {

		if (contratoManual != null && contratoManual.getSituacao().equals(DominioSituacaoEnvioContrato.E)) {
			contratoEnviado = true;
		} else {
			contratoEnviado = false;
		}
		verificaUasgRespLicitacaoObrigatorio();
		calculaPeriodoDescritivo();

		// List<ScoAutorizacaoForn> res = siconFacade.listarAfByFornAndLicAll(
		// contratoManual.getLicitacao(), contratoManual.getFornecedor());
		List<ScoAfContrato> res = siconFacade.obterAfByContrato(contratoManual);

		updateDataListModel(res);
	}

	
	private void updateDataListModel(List<ScoAfContrato> res) {

		List<ContratoLicitacaoVO> _tempAfsList = new ArrayList<ContratoLicitacaoVO>();
		for (ScoAfContrato a : res) {
			ContratoLicitacaoVO c = new ContratoLicitacaoVO(a.getScoAutorizacoesForn(), DominioSimNao.S);
			_tempAfsList.add(c);
		}
		afs = _tempAfsList;

	}

	private void preparaInclusaoContratoManual() {

		contratoManual = new ScoContrato();
		contratoManual.setSituacao(DominioSituacaoEnvioContrato.A);
		contratoManual.setIndLicitar(DominioSimNao.S);
		contratoManual.setIndAditivar(DominioSimNao.S);
		contratoManual.setIndRecDep(DominioReceitaDespesa.D);
		contratoManual.setIndFixoDemanda(DominioFixoDemanda.F);
		contratoManual.setIndOrigem(DominioOrigemContrato.M);
		listaItensContrato = new ArrayList<ScoItensContrato>();

		verificaModalidadeObrigatoria();
		verificaIncisoObrigatorio();

		contratoEnviado = false;
		periodoDescritivo = " ";
		edicao = false;
	}

	private void preparaAlteracaoContratoManual() {

		if (contratoManual != null && contratoManual.getSituacao().equals(DominioSituacaoEnvioContrato.E)) {
			contratoEnviado = true;
		} else {
			contratoEnviado = false;
		}

		verificaModalidadeObrigatoria();
		verificaIncisoObrigatorio();
		verificaUasgRespLicitacaoObrigatorio();
		calculaPeriodoDescritivo();

		if (contratoManual != null) {
			pesquisarItensContrato(contratoManual);
		} else {
			listaItensContrato = new ArrayList<ScoItensContrato>();
		}

		// periodoDescritivo = " ";
		edicao = true;
	}

	// actions
	public void inserirContratoManual() {

		try {

			if (numLicitacao != null) {
				final ScoLicitacao licitacao = new ScoLicitacao();
				licitacao.setNumero(numLicitacao);
				contratoManual.setLicitacao(licitacao);
			}

			siconFacade.inserirContratoManual(contratoManual);

			nrContrato = contratoManual.getNrContrato();

			preparaAlteracaoContratoManual();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CONTRATO_MANUAL");

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		setValorAlterado(false);
	}

	public void alterarContratoManual() {

		try {

			boolean isContAut = false;
			if (contratoManual.getIndOrigem() != DominioOrigemContrato.A) {
				if (numLicitacao != null) {
					final ScoLicitacao licitacao = new ScoLicitacao();
					licitacao.setNumero(numLicitacao);
					contratoManual.setLicitacao(licitacao);
				} else {
					contratoManual.setLicitacao(null);
				}
			} else {
				isContAut = true;
			}
			siconFacade.alterarContratoManual(contratoManual);
			if (!isContAut) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CONTRATO_MANUAL");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CONT_AUTO");
			}

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		setValorAlterado(false);		
	}

	public String manterItensContratoManual() {
		return PAGE_MANTER_ITENS_CONTRATO;
	}

	public void limpar() {
		siconFacade.desatacharContrato(contratoManual);
		contratoManual = null;
		numLicitacao = null;
		
		if (edicao){
			iniciar();
		} else {		
			listaItensContrato = new ArrayList<ScoItensContrato>();
			preparaInclusaoContratoManual();
		}
	}

	public String verificaAlteracoes() {
		if (isValorAlterado()) {
			setConfirmarVolta(true);
			//return null;
			return "gerenciarContratos";
		} else {
			return voltar();
		}
	}

	public String voltar() {
		siconFacade.desatacharContrato(contratoManual);
		contratoManual = new ScoContrato();
		nrContrato = null;
		return voltarParaUrl;
	}

	public void definirValorAlterado() {
		setValorAlterado(true);
	}

	public String getDescricaoForn() {
		if (this.contratoManual != null && this.contratoManual.getFornecedor() != null) {
			if (this.contratoManual.getFornecedor().getCgc() != null
					&& this.contratoManual.getFornecedor().getCgc().intValue() != 0) {
				return CoreUtil.formatarCNPJ(this.contratoManual.getFornecedor().getCgc()) + " - "
						+ this.contratoManual.getFornecedor().getRazaoSocial();
			} else if (this.contratoManual.getFornecedor().getCpf() != null
					&& this.contratoManual.getFornecedor().getCpf().intValue() != 0) {
				return CoreUtil.formataCPF(this.contratoManual.getFornecedor().getCpf()) + " - "
						+ this.contratoManual.getFornecedor().getRazaoSocial();
			}
		}
		return "";
	}

	public String formataCPF(final Long cpf) {
		if (cpf.intValue() != 0) {
			return CoreUtil.formataCPF(cpf);
		} else {
			return "";
		}
	}

	public String formataCNPJ(final Long cnpj) {
		if (cnpj.intValue() != 0) {
			return CoreUtil.formatarCNPJ(cnpj);
		} else {
			return "";
		}
	}

	public String redirectItensAf() {
		return PAGE_MANTER_AF_CONTRATO;
	}

	public void ajustaValorGarantia() {
		if (contratoManual != null) {
			setTipoGarantiaInexistente(DominioTipoGarantia.I.equals(contratoManual.getIndTipoGarantia()));
			if (isTipoGarantiaInexistente()) {
				contratoManual.setValorGarantia(BigDecimal.ZERO);
			}
		}
	}

	public List<ScoTipoContratoSicon> listarTiposContratoAtivos(final String pesquisa) {
		final List<ScoTipoContratoSicon> tiposContrato;

		if (contratoManual.getIndOrigem() == DominioOrigemContrato.M && !siconFacade.possuiItensContrato(contratoManual)) {
			tiposContrato = cadastrosBasicosSiconFacade.listarTiposContrato(pesquisa);
		} else {
			tiposContrato = cadastrosBasicosSiconFacade.listarTiposContratoSemAditivoInsItens(pesquisa);
		}
		return tiposContrato;
	}

	public List<ScoModalidadeLicitacao> listarModalidadeLicitacaoAtivas(final String pesquisa) {
		final List<ScoModalidadeLicitacao> modalidades = comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(pesquisa);
		return modalidades;
	}

	public List<ScoFornecedor> listarFornecedoresAtivos(final String pesquisa) {

		setValorAlterado(true);

		final List<ScoFornecedor> fornecedores = comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, false);
		return fornecedores;
	}

	/*
	 * Utilizado pela Suggestion que foi substituída por um campo input simples
	 * public List<ScoLicitacao> listarLicitacoes(Object pesquisa){
	 * List<ScoLicitacao> licitacoes = siconFacade.listarLicitacoes(pesquisa);
	 * return licitacoes; }
	 */

	public List<ScoCriterioReajusteContrato> listarCriteriosReajusteContratoAtivos(final String pesquisa) {

		setValorAlterado(true);

		final List<ScoCriterioReajusteContrato> criteriosReajuste = cadastrosBasicosSiconFacade
				.listarCriteriosReajusteContratoAtivos(pesquisa);
		return criteriosReajuste;
	}

	public List<RapServidores> pesquisarServidorAtivoGestor(final String paramPesquisa) {

		setValorAlterado(true);

		try {
			return siconFacade.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public List<RapServidores> pesquisarServidorAtivoFiscal(final String paramPesquisa) {

		setValorAlterado(true);

		try {
			return siconFacade.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public void verificaModalidadeObrigatoria() {

		modalidadeObrigatoria = false;

		if (contratoManual != null && contratoManual.getTipoContratoSicon() != null
				&& contratoManual.getTipoContratoSicon().getIndModalidade()) {
			modalidadeObrigatoria = true;
		}

		// Alterou campo em ação de inclusão
		if (contratoManual != null && contratoManual.getTipoContratoSicon() != null && contratoManual.getSeq() == null) {
			setValorAlterado(true);
		}
	}

	public void verificaIncisoObrigatorio() {

		incisoObrigatorio = false;

		if (contratoManual != null && contratoManual.getModalidadeLicitacao() != null
				&& contratoManual.getModalidadeLicitacao().getArtigo()) {
			incisoObrigatorio = true;
		}

		// Alterou campo em ação de inclusão
		if (contratoManual != null && contratoManual.getModalidadeLicitacao() != null && contratoManual.getSeq() == null) {
			setValorAlterado(true);
		}
		
	}

	public void verificaUasgRespLicitacaoObrigatorio() {
		if (contratoManual != null && contratoManual.getUasgSubrog() != null) {
			setUasgRespLicitacaoObrigatorio(true);
		} else {
			setUasgRespLicitacaoObrigatorio(false);
		}
	}

	public void calculaPeriodoDescritivo() {

		periodoDescritivo = calcDiff(contratoManual.getDtInicioVigencia(), contratoManual.getDtFimVigencia());
	}

	private String calcDiff(final Date d1, final Date d2) {
		if (d1 != null ^ d2 != null) {
			return "Período inválido";
		} else if (d1 == null && d2 == null) {
			return "";
		} else {
			if (d1.compareTo(d2) > 0) {
				return "Período inválido";
			}
			final Period p = new Period(d1.getTime(), d2.getTime(), PeriodType.yearMonthDay());
			final StringBuffer s = new StringBuffer();
			if (p.getYears() > 0) {
				s.append(p.getYears());
				if (p.getYears() == 1) {
					s.append(" ano ");
				} else {
					s.append(" anos ");
				}
			}
			if (p.getMonths() > 0) {
				s.append(p.getMonths());
				if (p.getMonths() == 1) {
					s.append(" mês ");
				} else {
					s.append(" meses ");
				}
			}
			if (p.getDays() > 0) {
				s.append(p.getDays());
				if (p.getDays() == 1) {
					s.append(" dia ");
				} else {
					s.append(" dias ");
				}
			}
			return s.toString();
		}
	}

	public void pesquisarItensContrato(final ScoContrato contrato) {
		try {
			listaItensContrato = siconFacade.listarItensContratos(contrato);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean materialPossuiCodSiasg(ScoMaterial material) {
		return this.cadastrosBasicosSiconFacade.pesquisarMaterialSicon(null, material, DominioSituacao.A, null) != null;
	}

	public boolean servicoPossuiCodSiasg(ScoServico servico) {
		return this.cadastrosBasicosSiconFacade.pesquisarServicoSicon(null, servico, DominioSituacao.A, null) != null;
	}

	// gets e sets
	public ScoContrato getContratoManual() {
		return contratoManual;
	}

	public void setContratoManual(final ScoContrato contratoManual) {
		this.contratoManual = contratoManual;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(final boolean edicao) {
		this.edicao = edicao;
	}

	public String getPeriodoDescritivo() {
		return periodoDescritivo;
	}

	public void setPeriodoDescritivo(final String periodoDescritivo) {
		this.periodoDescritivo = periodoDescritivo;
	}

	public boolean isIncisoObrigatorio() {
		return incisoObrigatorio;
	}

	public void setIncisoObrigatorio(final boolean incisoObrigatorio) {
		this.incisoObrigatorio = incisoObrigatorio;
	}

	public boolean isContratoEnviado() {
		return contratoEnviado;
	}

	public void setContratoEnviado(final boolean contratoEnviado) {
		this.contratoEnviado = contratoEnviado;
	}

	public Long getNrContrato() {
		return nrContrato;
	}

	public void setNrContrato(final Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	public Integer getNumLicitacao() {
		return numLicitacao;
	}

	public void setNumLicitacao(final Integer numLicitacao) {
		this.numLicitacao = numLicitacao;
	}

	public boolean isModalidadeObrigatoria() {
		return modalidadeObrigatoria;
	}

	public void setModalidadeObrigatoria(final boolean modalidadeObrigatoria) {
		this.modalidadeObrigatoria = modalidadeObrigatoria;
	}

	public List<ScoItensContrato> getListaItensContrato() {
		return listaItensContrato;
	}

	public void setListaItensContrato(final List<ScoItensContrato> listaItensContrato) {
		this.listaItensContrato = listaItensContrato;
	}

	public boolean isUasgRespLicitacaoObrigatorio() {
		return uasgRespLicitacaoObrigatorio;
	}

	public void setUasgRespLicitacaoObrigatorio(final boolean uasgRespLicitacaoObrigatorio) {
		this.uasgRespLicitacaoObrigatorio = uasgRespLicitacaoObrigatorio;
	}

	public List<ContratoLicitacaoVO> getAfs() {
		return afs;
	}

	public void setAfs(List<ContratoLicitacaoVO> afs) {
		this.afs = afs;
	}

	public ContratoLicitacaoVO getAf() {
		return af;
	}

	public void setAf(ContratoLicitacaoVO af) {
		this.af = af;
	}

	public boolean isValorAlterado() {
		return valorAlterado;
	}

	public void setValorAlterado(boolean valorAlterado) {
		this.valorAlterado = valorAlterado;
	}

	public boolean isConfirmarVolta() {
		return confirmarVolta;
	}

	public void setConfirmarVolta(boolean confirmarVolta) {
		this.confirmarVolta = confirmarVolta;
	}

	public boolean isTipoGarantiaInexistente() {
		return tipoGarantiaInexistente;
	}

	public void setTipoGarantiaInexistente(boolean tipoGarantiaInexistente) {
		this.tipoGarantiaInexistente = tipoGarantiaInexistente;
	}

	public boolean isSatisfazRn02() {
		return satisfazRn02;
	}

	public void setSatisfazRn02(boolean satisfazRn02) {
		this.satisfazRn02 = satisfazRn02;
	}

	public boolean isbSituacaoDoContrato() {
		return bSituacaoDoContrato;
	}

	public void setbSituacaoDoContrato(boolean bSituacaoDoContrato) {
		this.bSituacaoDoContrato = bSituacaoDoContrato;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

}
