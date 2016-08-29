package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioIntermediarioLancamentosContaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioIntermediarioLancamentosContaPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7548765961782043426L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioIntermediarioLancamentosContaPdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private Integer cthSeq;

	private Integer prontuario;

	private Integer codPaciente;

	private String nomePaciente;

	private Date dtHrInternacao;

	private Date dtHrAlta;

	private Integer contaHospitalarFiltro;

	private Boolean isDirectPrint;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public String inicio() {

		if (codPaciente != null && prontuario == null) {
			AipPacientes pac = pacienteFacade.obterAipPacientesPorChavePrimaria(codPaciente);
			codPaciente = pac.getCodigo();
			prontuario = pac.getProntuario();
			nomePaciente = pac.getNome();

		} else if (prontuario != null && codPaciente == null) {
			AipPacientes pac = pacienteFacade.obterPacientePorProntuario(prontuario);
			codPaciente = pac.getCodigo();
			prontuario = pac.getProntuario();
			nomePaciente = pac.getNome();
		}

		if (cthSeq != null) {
			FatContasHospitalares contaHospitalar = faturamentoFacade.obterContaHospitalar(cthSeq);
			if (contaHospitalar != null) {
				dtHrAlta = contaHospitalar.getDtAltaAdministrativa();
				dtHrInternacao = contaHospitalar.getDataInternacaoAdministrativa();
			}
		}

		if (isDirectPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				return null;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		return null;
	}

	@Override
	public Collection<RelatorioIntermediarioLancamentosContaVO> recuperarColecao() {
		List<RelatorioIntermediarioLancamentosContaVO> lista = new ArrayList<RelatorioIntermediarioLancamentosContaVO>();
		try {

			Map<AghuParametrosEnum, AghParametros> parametros = new HashMap<AghuParametrosEnum, AghParametros>();
			parametros.put(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO,
					parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO));
			parametros.put(AghuParametrosEnum.P_CONVENIO_SUS,
					parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS));
			parametros.put(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS,
					parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS));
			lista = faturamentoFacade.obterItensContaParaRelatorioIntermediarioLancamentos(cthSeq, parametros);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		// Melhoria #24485 : exibe somente as linhas quando não houver dados
		// para o relatório.
		if (lista.isEmpty()) {
			RelatorioIntermediarioLancamentosContaVO relVO = new RelatorioIntermediarioLancamentosContaVO();
			lista.add(relVO);
		}

		return lista;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioIntermediarioLancamentosConta.jasper";
	}

	public String voltar() {
		return "relatorioIntermediarioLancamentosConta";
	}

	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("prontuario", prontuario);
		params.put("cthSeq", cthSeq);
		params.put("codPaciente", codPaciente);
		params.put("nomePaciente", nomePaciente);
		params.put("dtHrAlta", dtHrAlta);
		params.put("dtHrInternacao", dtHrInternacao);

		String subReport = "br/gov/mec/aghu/faturamento/report/subRelatorioIntermediarioLancamentosConta.jasper";
		params.put("subRelatorio", Thread.currentThread().getContextClassLoader().getResourceAsStream(subReport));

		AghParametros prmLinhas = null;
		try {
			prmLinhas = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_LINHAS_BOLETIM_INTERM_LANC);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		final Integer nrLinhas = prmLinhas.getVlrNumerico().intValue();
		final List<RelatorioIntermediarioLancamentosContaVO> linhas = new ArrayList<RelatorioIntermediarioLancamentosContaVO>(nrLinhas);
		for (int i = 0; i < nrLinhas; i++) {
			linhas.add(new RelatorioIntermediarioLancamentosContaVO());
		}
		params.put("linhas", linhas);

		return params;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Integer getContaHospitalarFiltro() {
		return contaHospitalarFiltro;
	}

	public void setContaHospitalarFiltro(Integer contaHospitalarFiltro) {
		this.contaHospitalarFiltro = contaHospitalarFiltro;
	}

	public Date getDtHrInternacao() {
		return dtHrInternacao;
	}

	public void setDtHrInternacao(Date dtHrInternacao) {
		this.dtHrInternacao = dtHrInternacao;
	}

	public Date getDtHrAlta() {
		return dtHrAlta;
	}

	public void setDtHrAlta(Date dtHrAlta) {
		this.dtHrAlta = dtHrAlta;
	}

}
