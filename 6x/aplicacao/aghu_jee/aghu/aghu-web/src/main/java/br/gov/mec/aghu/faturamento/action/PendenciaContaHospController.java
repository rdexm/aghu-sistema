package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatMotivoPendencia;
import br.gov.mec.aghu.model.FatPendenciaContaHosp;
import br.gov.mec.aghu.model.FatPendenciaContaHospId;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PendenciaContaHospController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	@Inject @Paginator
	private DynamicDataModel<FatPendenciaContaHosp> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5472176457446208166L;

	private final static String PENDENCIAS_ADM = "faturamento-pendenciasAdm";

	private final static String NOVA_PENDENCIA_CONTA_HOSP = "faturamento-pendenciaContaHospEdicao";

	private FatMotivoPendencia motivoPendencia;

	private AghUnidadesFuncionais unidadeFuncional;

	private VFatContaHospitalarPac vFatContaHospPac;

	private FatPendenciaContaHosp fatPendenciaContaHosp;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;


	private Short seqp;

	private String origem;

	private Integer cthSeq;

	private String indSituacao;

	private boolean editavel;

	public void inicio() {
	 

		dataModel.setPesquisaAtiva(true);
		dataModel.reiniciarPaginator();
		editavel = true;
		motivoPendencia = null;
		unidadeFuncional = null;
		fatPendenciaContaHosp = new FatPendenciaContaHosp();
		fatPendenciaContaHosp.setIndSituacao(DominioSituacao.A);
		fatPendenciaContaHosp.setFatMotivoPendencia(new FatMotivoPendencia());
		setFatPendenciaContaHosp(fatPendenciaContaHosp);
		setvFatContaHospPac(faturamentoFacade.obterContaHospitalarPaciente(cthSeq));

		if (vFatContaHospPac != null
				&& (!DominioSituacaoConta.A.equals(vFatContaHospPac.getIndSituacao()) && !DominioSituacaoConta.F.equals(vFatContaHospPac
						.getIndSituacao()))) {
			editavel = false;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALTERACAO_PEND_ADM_ATIVA_INATIVA");
		}
	
	}

	public String novaPendenciaContaHosp() {
		return NOVA_PENDENCIA_CONTA_HOSP;
	}

	public String confirmar() {
		try {
			faturamentoFacade.inserirFatPendenciaContaHospitalar(cthSeq, motivoPendencia,
					(unidadeFuncional != null ? unidadeFuncional.getSeq() : null), fatPendenciaContaHosp.getIndSituacao());
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_PENDENCIA_ADMINISTRATIVA");

			return origem;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String ativarInativarPendenciaAdm() {
		try {
			populaFatPendenciaContaHospitalar();
			fatPendenciaContaHosp.setIndSituacao(DominioSituacao.valueOf(indSituacao));
			faturamentoFacade.alterarFatPendenciaContaHospitalar(fatPendenciaContaHosp);

			if (DominioSituacao.A.equals(fatPendenciaContaHosp.getIndSituacao())) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVACAO_PENDENCIA_ADMINISTRATIVA");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INATIVACAO_PENDENCIA_ADMINISTRATIVA");
			}

			return PENDENCIAS_ADM;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	private void populaFatPendenciaContaHospitalar() {
		if (cthSeq != null && seqp != null) {
			fatPendenciaContaHosp.setId(new FatPendenciaContaHospId(cthSeq, seqp));
			fatPendenciaContaHosp = faturamentoFacade.obterFatPendenciaContaHosp(fatPendenciaContaHosp);
		}
	}

	public Long pesquisarCountMotivosPendencia(final String filtro) {
		return faturamentoFacade.listarMotivosPendenciaPorSeqOuDescricaoCount(filtro);
	}

	public List<FatMotivoPendencia> pesquisarMotivosPendencia(final String filtro) {
		return  this.returnSGWithCount(faturamentoFacade.listarMotivosPendenciaPorSeqOuDescricao(filtro),pesquisarCountMotivosPendencia(filtro));
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String paramPesquisa) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(paramPesquisa),pesquisarUnidadeFuncionalCount(paramPesquisa));
	}

	public Long pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa);
	}

	@Override
	public List<FatPendenciaContaHosp> recuperarListaPaginada(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return faturamentoFacade.listarFatPendenciaContaHospPorCthSeq(firstResult, maxResult, orderProperty, asc, cthSeq);
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.listarFatPendenciaContaHospPorCthSeqCount(cthSeq);
	}

	public String voltar() {
		this.cthSeq = null;
		return origem;
	}

    public String voltarPendenciaAdm() {
        return PENDENCIAS_ADM;
    }

    public FatPendenciaContaHosp getFatPendenciaContaHosp() {
		return fatPendenciaContaHosp;
	}

	public void setFatPendenciaContaHosp(FatPendenciaContaHosp fatPendenciaContaHosp) {
		this.fatPendenciaContaHosp = fatPendenciaContaHosp;
	}

	public VFatContaHospitalarPac getvFatContaHospPac() {
		return vFatContaHospPac;
	}

	public void setvFatContaHospPac(VFatContaHospitalarPac vFatContaHospPac) {
		this.vFatContaHospPac = vFatContaHospPac;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public FatMotivoPendencia getMotivoPendencia() {
		return motivoPendencia;
	}

	public void setMotivoPendencia(FatMotivoPendencia motivoPendencia) {
		this.motivoPendencia = motivoPendencia;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isEditavel() {
		return editavel;
	}

	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}

	public DynamicDataModel<FatPendenciaContaHosp> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatPendenciaContaHosp> dataModel) {
		this.dataModel = dataModel;
	}
}
