package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.TipoArquivoRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioGerarArquivoProducaoPHIController extends ActionController {

	private static final long serialVersionUID = 6941388487383615914L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioGerarArquivoProducaoPHIController.class);

	private Log getLog() {
		return LOG;
	}

	public enum RelatorioGerarArquivoProducaoPHIControllerExceptionCode implements BusinessExceptionCode {
		LABEL_DATA_FINAL_INVALIDA, MSG_IMPOSSIVEL_EXCLUIR, MSG_LISTA_VAZIA, MSG_DT_INICIO_OBRIGATORIO, PROBLEMA_CRIACAO_ARQUIVO;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	// Campos auxliares de suggestions
	private Short cpgCphCspCnvCodigo;

	private Short cpgGrcSeq;

	private Byte cpgCphCspSeq;

	// Procedimento Realizado
	private VFatAssociacaoProcedimento procedimento;

	private List<VFatAssociacaoProcedimento> listaProcedimentos;

	private Integer indice;

	// private FatCompetencia competencia;
	private Date dtInicio;

	private Date dtFinal;

	private String file;

	private Boolean gerouArquivo;
	
	@PostConstruct
	protected void init(){
		begin(conversation,true);
		inicio();
	}

	public void inicio() {
		inicializarParametros();
		setDtInicio(null);
		setDtFinal(null);
		setProcedimento(null);
		setListaProcedimentos(new ArrayList<VFatAssociacaoProcedimento>());
	}


	public void adicionarProcedimento() {
		if (getProcedimento() != null) {
			getListaProcedimentos().add(getProcedimento());
			setProcedimento(null);
		}
	}

	public void excluirProcedimento() {
		if (getListaProcedimentos().isEmpty() || getIndice() == null
				|| getIndice().intValue() >= getListaProcedimentos().size() || getIndice() < 0) {
			apresentarMsgNegocio(Severity.ERROR, RelatorioGerarArquivoProducaoPHIControllerExceptionCode.MSG_IMPOSSIVEL_EXCLUIR.toString());
		} else {
			getListaProcedimentos().remove(getIndice().intValue());
		}
	}

	private List<Integer> getPhiSeqs(final List<VFatAssociacaoProcedimento> listaProcedimentos) {
		if (listaProcedimentos == null || listaProcedimentos.isEmpty()) {
			return new ArrayList<Integer>(0);
		}
		final List<Integer> retorno = new ArrayList<Integer>(listaProcedimentos.size());
		for (final VFatAssociacaoProcedimento procedimento : listaProcedimentos) {
			retorno.add(procedimento.getId().getPhiSeq());
		}
		return retorno;
	}

	public void dispararDownload() {
		try {
			download(file, DominioNomeRelatorio.NOME_ARQUIVO_PRODUCAO_PHI.getDescricao() + DateUtil.dataToString(new Date(), "ddMMyyyy")
					+ "." + TipoArquivoRelatorio.CSV.getExtensao(), "text/csv");
			setGerouArquivo(false);
			file = null;
		} catch (final IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(RelatorioGerarArquivoProducaoPHIControllerExceptionCode.PROBLEMA_CRIACAO_ARQUIVO, e));
		}
	}

	public void gerarArquivo() {
		boolean ok = true;
		if (getDtInicio() == null) {
			apresentarMsgNegocio(Severity.ERROR,
					RelatorioGerarArquivoProducaoPHIControllerExceptionCode.MSG_DT_INICIO_OBRIGATORIO.toString(), "valorDataInicio");
			ok = false;
		} else {
			if (getDtFinal() != null && getDtInicio().compareTo(getDtFinal()) > 0) {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioGerarArquivoProducaoPHIControllerExceptionCode.LABEL_DATA_FINAL_INVALIDA.toString());
				ok = false;
			}
		}
		if (this.getListaProcedimentos().isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, RelatorioGerarArquivoProducaoPHIControllerExceptionCode.MSG_LISTA_VAZIA.toString(),
					"procedimento");
			ok = false;
		}

		if (ok) {
			try {
				file = faturamentoFacade.gerarArquivoProducaoPHI(getPhiSeqs(getListaProcedimentos()), getDtInicio(),
						getDtFinal());
				setGerouArquivo(true);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	// Suggestions
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(final String strPesquisa) {
		try {
			if (cpgCphCspCnvCodigo == null || cpgCphCspSeq == null || cpgGrcSeq == null) {
				inicializarParametros();
			}
			return this.returnSGWithCount(faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(strPesquisa, cpgCphCspCnvCodigo,
					this.cpgGrcSeq, this.cpgCphCspSeq),listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(strPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private void inicializarParametros() {
		try {
			cpgCphCspCnvCodigo = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
			cpgCphCspSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico().byteValue();
			cpgGrcSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(final String strPesquisa) {
		try {
			if (cpgCphCspCnvCodigo == null || cpgCphCspSeq == null || cpgGrcSeq == null) {
				inicializarParametros();
			}
			return faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(strPesquisa, cpgCphCspCnvCodigo,
					cpgGrcSeq, cpgCphCspSeq);
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public void setProcedimento(final VFatAssociacaoProcedimento procedimento) {
		this.procedimento = procedimento;
	}

	public VFatAssociacaoProcedimento getProcedimento() {
		return procedimento;
	}

	public void setDtInicio(final Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtFinal(final Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setListaProcedimentos(final List<VFatAssociacaoProcedimento> listaProcedimentos) {
		this.listaProcedimentos = listaProcedimentos;
	}

	public List<VFatAssociacaoProcedimento> getListaProcedimentos() {
		return listaProcedimentos;
	}

	public void setIndice(final Integer indice) {
		this.indice = indice;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setGerouArquivo(final Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

}