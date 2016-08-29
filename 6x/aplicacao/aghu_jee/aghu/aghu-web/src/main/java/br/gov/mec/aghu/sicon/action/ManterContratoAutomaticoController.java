package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.dominio.DominioFixoDemanda;
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
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoLicitacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterContratoAutomaticoController extends ActionController {

	private static final long serialVersionUID = -1991142250572704621L;
	
	private static final String PAGE_GERENCIAR_CONTRATOS = "gerenciarContratos";
	private static final String PAGE_MANTER_AF_CONTRATO = "manterAfContrato";
	private static final String PAGE_MANTER_AF_CONTRATO_AUTOMATICO = "manterContratoAutomatico";

	private List<ContratoLicitacaoVO> afsSelec;

	private List<ContratoLicitacaoVO> afsvinculados;

	private List<ContratoLicitacaoVO> afs;

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	private ScoContrato contratoAuto;
	private String periodoDescritivo;

	private ScoFornecedor forn;
	private ScoLicitacao licitacao;

	private ContratoLicitacaoVO af;

	private boolean tipoGarantiaInexistente;

	private Long nrContrato;

	private boolean contratoEnviado;

	private boolean satisfazRn02;

	private String voltarParaUrl;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private void situacaoContrato() {
		if (contratoAuto != null
				&& (contratoAuto.getSituacao().equals(DominioSituacaoEnvioContrato.E) || contratoAuto.getSituacao().equals(
						DominioSituacaoEnvioContrato.AR))) {
			contratoEnviado = true;
		} else {
			contratoEnviado = false;
		}
	}

	private void validaRn02() {
		if (contratoAuto != null && contratoEnviado && !contratoAuto.getAditivos().isEmpty()) {
			satisfazRn02 = true;
		} else {
			satisfazRn02 = false;
		}
	}

	public void init() {

		if (nrContrato != null) {
			contratoAuto = siconFacade.obterContratoPorNumeroContrato(nrContrato);
			preparaAlteracaoContratoAuto();
		} else {
			afsvinculados = new ArrayList<ContratoLicitacaoVO>();

			if (getAfsSelec() != null && !getAfsSelec().isEmpty()) {
				for (ContratoLicitacaoVO cvo : getAfsSelec()) {
					if (DominioSimNao.S.equals(cvo.getVincularAoContrato())) {
						afsvinculados.add(cvo);
					}
				}
				this.forn = getAfsSelec().get(0).getAf().getPropostaFornecedor().getFornecedor();
				this.licitacao = getAfsSelec().get(0).getAf().getPropostaFornecedor().getLicitacao();
			}
			
			setAfs(null);
		}
		if (contratoAuto == null) {
			contratoAuto = new ScoContrato();
			contratoAuto.setModalidadeLicitacao(this.licitacao.getModalidadeLicitacao());
			contratoAuto.setFornecedor(this.forn);
			contratoAuto.setInciso(this.licitacao.getIncisoArtigoLicitacao());
			contratoAuto.setValorTotal(somaValores());
			contratoAuto.setSituacao(DominioSituacaoEnvioContrato.A);
			contratoAuto.setLicitacao(licitacao);
			contratoAuto.setIndLicitar(DominioSimNao.S);
			contratoAuto.setIndAditivar(DominioSimNao.S);
			contratoAuto.setIndRecDep(DominioReceitaDespesa.D);
			contratoAuto.setIndFixoDemanda(DominioFixoDemanda.F);
		}
		ajustaValorGarantia();
		situacaoContrato();
		validaRn02();
	
	}

	private void preparaAlteracaoContratoAuto() {

		calculaPeriodoDescritivo();

		List<ScoAfContrato> res = siconFacade.obterAfByContrato(contratoAuto);
		
		updateDataListModel(res);
	}

	
	private void updateDataListModel(List<ScoAfContrato> res) {

		List<ContratoLicitacaoVO> _tempAfsList = new ArrayList<ContratoLicitacaoVO>();
		for (ScoAfContrato a : res) {
			a.getScoAutorizacoesForn().setItensAutorizacaoForn(siconFacade.obterItemAfAtivoPorAf(a));
			ContratoLicitacaoVO c = new ContratoLicitacaoVO(a.getScoAutorizacoesForn(), DominioSimNao.S);
			_tempAfsList.add(c);
		}
		setAfs(_tempAfsList);
	}

	public String ajustaValorGarantia() {
		if (contratoAuto != null) {
			setTipoGarantiaInexistente(DominioTipoGarantia.I.equals(contratoAuto.getIndTipoGarantia()));
			if (isTipoGarantiaInexistente()) {
				contratoAuto.setValorGarantia(BigDecimal.ZERO);
			}
		}
		return null;
	}

	public String gravar() {
		try {

			if (this.nrContrato == null) {
				List<ScoAfContrato> afContratos = new ArrayList<ScoAfContrato>();
				for (ContratoLicitacaoVO af : afsvinculados) {
					ScoAfContrato afc = new ScoAfContrato();
					afc.setScoAutorizacoesForn(af.getAf());
					afContratos.add(afc);
				}
				this.contratoAuto.setScoAfContratos(afContratos);
				siconFacade.inserirContratoAutomatico(contratoAuto);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERCAO_CONT_AUTO");
			} else {
				siconFacade.alterarContratoAutomatico(contratoAuto);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CONT_AUTO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_GERENCIAR_CONTRATOS;
	}

	private BigDecimal somaValores() {
		BigDecimal res = BigDecimal.ZERO;
		for (ContratoLicitacaoVO vo : getAfsSelec()) {
			if (DominioSimNao.S.equals(vo.getVincularAoContrato())) {
				res = vo.getValorProposta().add(res);
			}
		}
		return res;
	}

	public String getDescricaoForn() {
		if (this.contratoAuto != null && this.contratoAuto.getFornecedor() != null) {
			if (this.contratoAuto.getFornecedor().getCgc() != null && this.contratoAuto.getFornecedor().getCgc().intValue() != 0) {
				return CoreUtil.formatarCNPJ(this.contratoAuto.getFornecedor().getCgc()) + " - "
						+ this.contratoAuto.getFornecedor().getRazaoSocial();
			} else if (this.contratoAuto.getFornecedor().getCpf() != null
					&& this.contratoAuto.getFornecedor().getCpf().intValue() != 0) {
				return CoreUtil.formataCPF(this.contratoAuto.getFornecedor().getCpf()) + " - "
						+ this.contratoAuto.getFornecedor().getRazaoSocial();
			}
		}
		return "";
	}

	public void calculaPeriodoDescritivo() {

		periodoDescritivo = calcDiff(contratoAuto.getDtInicioVigencia(), contratoAuto.getDtFimVigencia());
	}

	public String limpar() {
		siconFacade.desatacharContrato(contratoAuto);

		contratoAuto = null;		
		
		return PAGE_MANTER_AF_CONTRATO_AUTOMATICO;
	}

	public String redirectItensAf() {
		return PAGE_MANTER_AF_CONTRATO;
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

	public String voltar() {
		nrContrato = null;
		contratoAuto = null;
		afs = null;
		return voltarParaUrl;
	}

	public List<ScoTipoContratoSicon> listarTiposContratoAtivos(final String pesquisa) {
		final List<ScoTipoContratoSicon> tiposContrato = cadastrosBasicosSiconFacade.listarTiposContrato(pesquisa);
		return tiposContrato;
	}

	public List<ScoCriterioReajusteContrato> listarCriteriosReajusteContratoAtivos(final String pesquisa) {
		final List<ScoCriterioReajusteContrato> criteriosReajuste = cadastrosBasicosSiconFacade
				.listarCriteriosReajusteContratoAtivos(pesquisa);
		return criteriosReajuste;
	}

	public List<RapServidores> pesquisarServidorAtivoGestor(final String paramPesquisa) {

		try {
			return siconFacade.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public List<RapServidores> pesquisarServidorAtivoFiscal(final String paramPesquisa) {

		try {
			return siconFacade.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public boolean materialPossuiCodSiasg(ScoMaterial material) {
		return this.cadastrosBasicosSiconFacade.pesquisarMaterialSicon(null, material, DominioSituacao.A, null) != null;
	}

	public boolean servicoPossuiCodSiasg(ScoServico servico) {
		return this.cadastrosBasicosSiconFacade.pesquisarServicoSicon(null, servico, DominioSituacao.A, null) != null;
	}

	public ScoFornecedor getForn() {
		return forn;
	}

	public void setForn(ScoFornecedor forn) {
		this.forn = forn;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public ScoContrato getContratoAuto() {
		return contratoAuto;
	}

	public void setContratoAuto(ScoContrato contratoAuto) {
		this.contratoAuto = contratoAuto;
	}

	public String getPeriodoDescritivo() {
		return periodoDescritivo;
	}

	public void setPeriodoDescritivo(String periodoDescritivo) {
		this.periodoDescritivo = periodoDescritivo;
	}

	public List<ContratoLicitacaoVO> getAfsvinculados() {
		return afsvinculados;
	}

	public void setAfsvinculados(List<ContratoLicitacaoVO> afsvinculados) {
		this.afsvinculados = afsvinculados;
	}

	public ContratoLicitacaoVO getAf() {
		return af;
	}

	public void setAf(ContratoLicitacaoVO af) {
		this.af = af;
	}

	public boolean isTipoGarantiaInexistente() {
		return tipoGarantiaInexistente;
	}

	public void setTipoGarantiaInexistente(boolean tipoGarantiaInexistente) {
		this.tipoGarantiaInexistente = tipoGarantiaInexistente;
	}

	public Long getNrContrato() {
		return nrContrato;
	}

	public void setNrContrato(Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	public List<ContratoLicitacaoVO> getAfsSelec() {
		return afsSelec;
	}

	public void setAfsSelec(List<ContratoLicitacaoVO> afsSelec) {
		this.afsSelec = afsSelec;
	}

	public List<ContratoLicitacaoVO> getAfs() {
		return afs;
	}

	public void setAfs(List<ContratoLicitacaoVO> afs) {
		this.afs = afs;
	}

	public boolean isContratoEnviado() {
		return contratoEnviado;
	}

	public boolean isSatisfazRn02() {
		return satisfazRn02;
	}

	public void setSatisfazRn02(boolean satisfazRn02) {
		this.satisfazRn02 = satisfazRn02;
	}

	public void setContratoEnviado(boolean contratoEnviado) {
		this.contratoEnviado = contratoEnviado;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

}
