package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoDcih;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ReapresentarContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<FatItemContaHospitalar> dataModel;

	private static final Log LOG = LogFactory.getLog(ReapresentarContaHospitalarPaginatorController.class);

	private static final String PAGE_FATURAMENTO_PESQUISAR_CONTAS_HOSPITALARES_PARA_REAPRESENTAR = "faturamento-pesquisarContasHospitalaresParaReapresentar";

	private static final long serialVersionUID = 4701590136907494632L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer cthSeq;

	private String from;

	private VFatContaHospitalarPac contaHospitalar;

	private VFatAssociacaoProcedimento procedimentoHospitalarSolicitado;

	private VFatAssociacaoProcedimento procedimentoHospitalarRealizado;

	private boolean habilitaBotaoReapresentarConta;

	private boolean habilitaBotaoDesfazerReapresentacao;

	private boolean habilitaBotaoRejeitarConta;

	private FatMotivoRejeicaoConta motivoRejeicaoConta;

	private Boolean isMotivoRejeicaoContaObrigatorio = Boolean.TRUE;

	public String iniciar() {
		// this.ativo = true;

		if (this.cthSeq != null) {
			try {
				this.contaHospitalar = this.faturamentoFacade.obterVFatContaHospitalarPac(this.cthSeq);
			} catch (Exception e) {
				LOG.debug("Exceção ignorada. ", e);
			}
		}

		if (this.contaHospitalar == null) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_CONTA_HOSPITALAR", String.valueOf(this.cthSeq));

			return this.from;
		}

		if (this.contaHospitalar.getProcedimentoHospitalarInterno() != null) {
			try {
				List<VFatAssociacaoProcedimento> procedimento = this.faturamentoFacade.listarAssociacaoProcedimentoSUSSolicitado(this.contaHospitalar.getProcedimentoHospitalarInterno().getSeq());
				if (!procedimento.isEmpty()) {
					this.procedimentoHospitalarSolicitado = procedimento.get(0);
				}
			} catch (final BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				this.procedimentoHospitalarSolicitado = new VFatAssociacaoProcedimento();
			}
		}

		if (this.contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null) {
			try {
				List<VFatAssociacaoProcedimento> procedimento = this.faturamentoFacade.listarAssociacaoProcedimentoSUSRealizado(this.contaHospitalar.getProcedimentoHospitalarInternoRealizado()
						.getSeq(), this.cthSeq);
				if (!procedimento.isEmpty()) {
					this.procedimentoHospitalarRealizado = procedimento.get(0);
				}
			} catch (final BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				this.procedimentoHospitalarRealizado = new VFatAssociacaoProcedimento();
			}
		}

		this.motivoRejeicaoConta = this.contaHospitalar.getMotivoRejeicaoConta();

		this.eventPostQuery();

		this.verificarMotivoRejeicaoContaObrigatorio();

		this.dataModel.reiniciarPaginator();

		return null;
	}

	private void eventPostQuery() {
		if (DominioSituacaoConta.O.equals(this.contaHospitalar.getIndSituacao())) {
			this.habilitaBotaoReapresentarConta = true;
			this.habilitaBotaoDesfazerReapresentacao = false;
			this.habilitaBotaoRejeitarConta = true;
		} else if (DominioSituacaoConta.R.equals(this.contaHospitalar.getIndSituacao()) && DominioSituacaoDcih.R.equals(this.contaHospitalar.getDciIndSituacao())) {
			this.habilitaBotaoReapresentarConta = false;
			this.habilitaBotaoDesfazerReapresentacao = false;
			this.habilitaBotaoRejeitarConta = false;
		} else if (DominioSituacaoConta.R.equals(this.contaHospitalar.getIndSituacao())
				&& (this.contaHospitalar.getDciIndSituacao() == null || !DominioSituacaoDcih.R.equals(this.contaHospitalar.getDciIndSituacao()))) {
			this.habilitaBotaoReapresentarConta = false;
			this.habilitaBotaoDesfazerReapresentacao = true;
			this.habilitaBotaoRejeitarConta = false;
		} else {
			this.habilitaBotaoReapresentarConta = false;
			this.habilitaBotaoDesfazerReapresentacao = false;
			this.habilitaBotaoRejeitarConta = false;
		}
	}

	@Override
	public Long recuperarCount() {
		try {
			return faturamentoFacade.listarItensContaHospitalarCount(this.cthSeq, null, null, null, null, null, true, null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	@Override
	public List<FatItemContaHospitalar> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return faturamentoFacade.listarItensContaHospitalar(firstResult, maxResult, orderProperty, asc, this.cthSeq, null, null, null, null, null, true, null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatItemContaHospitalar>();
		}
	}

	public void reapresentarContaHospitalar() {
		try {

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			final Date dataFimVinculoServidor = new Date();
			this.faturamentoFacade.reapresentarContaHospitalar(this.contaHospitalar.getCthSeq(), this.motivoRejeicaoConta, nomeMicrocomputador, dataFimVinculoServidor);
			this.faturamentoFacade.clear();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONTA_HOSPITALAR_REAPRESENTADA");

			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			// this.reiniciarPaginator(PesquisarContasHospitalaresParaReapresentarPaginatorController.class);
			this.iniciar();
		} catch (BaseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		}
	}

	public void desfazerReapresentacaoContaHospitalar() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			final Date dataFimVinculoServidor = new Date();
			this.faturamentoFacade.desfazerReapresentacaoContaHospitalar(this.contaHospitalar.getCthSeq(), nomeMicrocomputador, dataFimVinculoServidor);
			this.faturamentoFacade.clear();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REAPRESENTACAO_CONTA_HOSPITALAR_DESFEITA");

			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			// this.reiniciarPaginator(PesquisarContasHospitalaresParaReapresentarPaginatorController.class);
			this.iniciar();

		} catch (BaseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		}
	}

	public void rejeitarContaHospitalar() {
		try {

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			final Date dataFimVinculoServidor = new Date();
			this.faturamentoFacade.rejeitarContaHospitalar(this.contaHospitalar.getCthSeq(), this.motivoRejeicaoConta, nomeMicrocomputador, dataFimVinculoServidor);
			this.faturamentoFacade.clear();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONTA_HOSPITALAR_REJEITADA");

			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			this.iniciar();

		} catch (BaseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		}
	}


	public void verificarMotivoRejeicaoContaObrigatorio() {
		if (DominioSituacaoConta.R != this.contaHospitalar.getIndSituacao() && this.motivoRejeicaoConta == null) {
			this.isMotivoRejeicaoContaObrigatorio = Boolean.TRUE;
		} else {
			this.isMotivoRejeicaoContaObrigatorio = Boolean.FALSE;
		}
	}

	// ## SUGGESTION BOX ##
	public Long pesquisarMotivosRejeicaoContaCount(String filtro) {
		return this.faturamentoFacade.pesquisarMotivosRejeicaoContaCount((String) filtro, DominioSituacao.A);
	}

	public List<FatMotivoRejeicaoConta> pesquisarMotivosRejeicaoConta(String filtro) {
		return  this.returnSGWithCount(this.faturamentoFacade.pesquisarMotivosRejeicaoConta((String) filtro, DominioSituacao.A, 0, 0, null, false),pesquisarMotivosRejeicaoContaCount(filtro));
	}

	public String voltar() {

		String retorno = this.from;

		this.cthSeq = null;
		this.from = null;
		this.contaHospitalar = null;
		this.procedimentoHospitalarSolicitado = null;
		this.procedimentoHospitalarRealizado = null;
		this.habilitaBotaoReapresentarConta = false;
		this.habilitaBotaoDesfazerReapresentacao = false;
		this.habilitaBotaoRejeitarConta = false;
		this.motivoRejeicaoConta = null;
		this.isMotivoRejeicaoContaObrigatorio = Boolean.TRUE;
		this.dataModel.limparPesquisa();

		return retorno != null ? retorno : PAGE_FATURAMENTO_PESQUISAR_CONTAS_HOSPITALARES_PARA_REAPRESENTAR;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public VFatContaHospitalarPac getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(VFatContaHospitalarPac contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalarSolicitado() {
		return procedimentoHospitalarSolicitado;
	}

	public void setProcedimentoHospitalarSolicitado(VFatAssociacaoProcedimento procedimentoHospitalarSolicitado) {
		this.procedimentoHospitalarSolicitado = procedimentoHospitalarSolicitado;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalarRealizado() {
		return procedimentoHospitalarRealizado;
	}

	public void setProcedimentoHospitalarRealizado(VFatAssociacaoProcedimento procedimentoHospitalarRealizado) {
		this.procedimentoHospitalarRealizado = procedimentoHospitalarRealizado;
	}

	public boolean isHabilitaBotaoReapresentarConta() {
		return habilitaBotaoReapresentarConta;
	}

	public void setHabilitaBotaoReapresentarConta(boolean habilitaBotaoReapresentarConta) {
		this.habilitaBotaoReapresentarConta = habilitaBotaoReapresentarConta;
	}

	public boolean isHabilitaBotaoDesfazerReapresentacao() {
		return habilitaBotaoDesfazerReapresentacao;
	}

	public void setHabilitaBotaoDesfazerReapresentacao(boolean habilitaBotaoDesfazerReapresentacao) {
		this.habilitaBotaoDesfazerReapresentacao = habilitaBotaoDesfazerReapresentacao;
	}

	public boolean isHabilitaBotaoRejeitarConta() {
		return habilitaBotaoRejeitarConta;
	}

	public void setHabilitaBotaoRejeitarConta(boolean habilitaBotaoRejeitarConta) {
		this.habilitaBotaoRejeitarConta = habilitaBotaoRejeitarConta;
	}

	public FatMotivoRejeicaoConta getMotivoRejeicaoConta() {
		return motivoRejeicaoConta;
	}

	public void setMotivoRejeicaoConta(FatMotivoRejeicaoConta motivoRejeicaoConta) {
		this.motivoRejeicaoConta = motivoRejeicaoConta;
	}

	public Boolean getIsMotivoRejeicaoContaObrigatorio() {
		return isMotivoRejeicaoContaObrigatorio;
	}

	public void setIsMotivoRejeicaoContaObrigatorio(Boolean isMotivoRejeicaoContaObrigatorio) {
		this.isMotivoRejeicaoContaObrigatorio = isMotivoRejeicaoContaObrigatorio;
	}

	public DynamicDataModel<FatItemContaHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItemContaHospitalar> dataModel) {
		this.dataModel = dataModel;
	}
}
