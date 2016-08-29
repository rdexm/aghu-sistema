package br.gov.mec.aghu.faturamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarContasHospitalaresParaReapresentarPaginatorController extends ActionController implements ActionPaginator {

	private static final String MENSAGEM_ERRO_CARREGAR_PARAMETRO = "MENSAGEM_ERRO_CARREGAR_PARAMETRO";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		init();
	}

	@Inject @Paginator
	private DynamicDataModel<VFatContaHospitalarPac> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarContasHospitalaresParaReapresentarPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7624725685642338793L;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer pacProntuario;

	private Integer pacCodigo;

	private Integer cthSeq;

	private Long cthNroAih;

	private DominioSituacaoConta situacao;

	private String codigoDcih;

	private Date dataInternacaoAdm;

	private Date dataAltaAdm;

	private VFatAssociacaoProcedimento procedimentoSolicitado;

	private VFatAssociacaoProcedimento procedimentoRealizado;

	private Short cpgCphCspCnvCodigo;

	private Byte cpgCphCspSeq;

	private Short cpgGrcSeq;

	private Short iphPhoSeq;

	public void init() {
		try {
			this.cpgCphCspCnvCodigo = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_CARREGAR_PARAMETRO, "P_CONVENIO_SUS");
		}

		try {
			this.cpgCphCspSeq = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO).getVlrNumerico().byteValue();
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_CARREGAR_PARAMETRO, "P_SUS_PLANO_INTERNACAO");
		}

		try {
			this.cpgGrcSeq = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_CARREGAR_PARAMETRO, "P_TIPO_GRUPO_CONTA_SUS");
		}

		try {
			this.iphPhoSeq = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_CARREGAR_PARAMETRO, "P_TABELA_FATUR_PADRAO");
		}
	}

	@Override
	public Long recuperarCount() {
		DominioSituacaoConta[] situacoes = this.situacao != null ? new DominioSituacaoConta[] { this.situacao } : null;
		return this.faturamentoFacade.pesquisarVFatContaHospitalarPacCount(this.pacProntuario, this.cthNroAih, this.pacCodigo, this.cthSeq, situacoes, this.codigoDcih, this.dataInternacaoAdm,
				this.dataAltaAdm, this.procedimentoSolicitado, this.procedimentoRealizado);
	}

	@Override
	public List<VFatContaHospitalarPac> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DominioSituacaoConta[] situacoes = this.situacao != null ? new DominioSituacaoConta[] { this.situacao } : null;

		return this.faturamentoFacade.pesquisarVFatContaHospitalarPac(this.pacProntuario, this.cthNroAih, this.pacCodigo, this.cthSeq, situacoes, this.codigoDcih, this.dataInternacaoAdm,
				this.dataAltaAdm, this.procedimentoSolicitado, this.procedimentoRealizado, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pacProntuario = null;
		this.pacCodigo = null;
		this.cthSeq = null;
		this.cthNroAih = null;
		this.situacao = null;
		this.codigoDcih = null;
		this.dataInternacaoAdm = null;
		this.dataAltaAdm = null;
		this.procedimentoSolicitado = null;
		this.procedimentoRealizado = null;
		this.dataModel.limparPesquisa();
	}

	public void pesquisar() {
		if (this.pacProntuario != null || this.pacCodigo != null || this.cthSeq != null || this.cthNroAih != null || this.dataInternacaoAdm != null || this.dataAltaAdm != null
				|| (this.codigoDcih != null && !this.codigoDcih.isEmpty())) {
			this.dataModel.reiniciarPaginator();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "FILTRO_OBRIGATORIO_PESQUISA_CONTA_PARA_REAPRESENTAR");
		}
	}

	// ## SUGGESTION BOX ##
	public Long pesquisarAssociacoesProcedimentosCount(String filtro) {
		return this.faturamentoFacade.pesquisarAssociacoesProcedimentosCount((String) filtro, null, null, this.cpgCphCspCnvCodigo, this.cpgCphCspSeq, null, null, this.cpgGrcSeq, this.iphPhoSeq);
	}

	public List<VFatAssociacaoProcedimento> pesquisarAssociacoesProcedimentos(String filtro) {
		return  this.returnSGWithCount(this.faturamentoFacade.pesquisarAssociacoesProcedimentos((String) filtro, null, null, this.cpgCphCspCnvCodigo, this.cpgCphCspSeq, null, null, this.cpgGrcSeq, this.iphPhoSeq),pesquisarAssociacoesProcedimentosCount(filtro));
	}

	public String buscaCodTabela(Integer phiSeq) {
		List<VFatAssociacaoProcedimento> lista = this.faturamentoFacade.pesquisarAssociacoesProcedimentos(null, phiSeq, null, this.cpgCphCspCnvCodigo, this.cpgCphCspSeq, null, null, this.cpgGrcSeq,
				this.iphPhoSeq);

		return lista != null && !lista.isEmpty() ? String.valueOf(lista.get(0).getId().getCodTabela()) : null;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Long getCthNroAih() {
		return cthNroAih;
	}

	public void setCthNroAih(Long cthNroAih) {
		this.cthNroAih = cthNroAih;
	}

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public Date getDataInternacaoAdm() {
		return dataInternacaoAdm;
	}

	public void setDataInternacaoAdm(Date dataInternacaoAdm) {
		this.dataInternacaoAdm = dataInternacaoAdm;
	}

	public Date getDataAltaAdm() {
		return dataAltaAdm;
	}

	public void setDataAltaAdm(Date dataAltaAdm) {
		this.dataAltaAdm = dataAltaAdm;
	}

	public String getCodigoDcih() {
		return codigoDcih;
	}

	public void setCodigoDcih(String codigoDcih) {
		this.codigoDcih = codigoDcih;
	}

	public VFatAssociacaoProcedimento getProcedimentoSolicitado() {
		return procedimentoSolicitado;
	}

	public void setProcedimentoSolicitado(VFatAssociacaoProcedimento procedimentoSolicitado) {
		this.procedimentoSolicitado = procedimentoSolicitado;
	}

	public VFatAssociacaoProcedimento getProcedimentoRealizado() {
		return procedimentoRealizado;
	}

	public void setProcedimentoRealizado(VFatAssociacaoProcedimento procedimentoRealizado) {
		this.procedimentoRealizado = procedimentoRealizado;
	}

	public DynamicDataModel<VFatContaHospitalarPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VFatContaHospitalarPac> dataModel) {
		this.dataModel = dataModel;
	}
}
