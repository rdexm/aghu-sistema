package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MpmAltaAtendMotivo;
import br.gov.mec.aghu.model.MpmAltaAtendRegistro;
import br.gov.mec.aghu.model.MpmAltaAtendimento;
import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.model.MpmAltaPrincExame;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaRespostaConsultoria;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmAltaTrgAlergia;
import br.gov.mec.aghu.model.MpmAltaTrgExame;
import br.gov.mec.aghu.model.MpmAltaTrgMedicacao;
import br.gov.mec.aghu.model.MpmAltaTrgSinalVital;
import br.gov.mec.aghu.model.MpmAltaTriagem;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioSumarioAltaAtendEmergenciaPOLRN extends BaseBusiness {

private static final String BR = "<br/>";

private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaAtendEmergenciaPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 7455152828354012067L;

	/**
	 * @ORADB - mamk_emg_visualiza.MAMC_EMG_VIS_ALT_TRG
	 */
	public String buscarDescricaoTriagem(MpmAltaSumarioId altaSumarioId,
			String pRelatorio) {

		StringBuilder vTexto = new StringBuilder(150);

		// Busca as informações da triagem - c_atg
		List<MpmAltaTriagem> altaTriagens = getPrescricaoMedicaFacade()
				.pesquisarAltaTriagemPeloMpmAltaSumarioId(altaSumarioId);

		for (MpmAltaTriagem altaTriagem : altaTriagens) {

			// Título
			if (("N").equals(pRelatorio)) {
				vTexto.append("<br/>Triagem -----------------------<br/>");
			}

			// Queixa principal
			vTexto.append("Queixa Principal: ").append(
					altaTriagem.getDescQueixa() ).append( "<br/><br/>");

			processarSinaisVitaisDescricaoTriagem(altaSumarioId, altaTriagem
					.getId().getSeqp(), vTexto);

			processarMedicamentosDescricaoTriagem(altaSumarioId, altaTriagem
					.getId().getSeqp(), vTexto);

			processarExamesDescricaoTriagem(altaSumarioId, altaTriagem.getId()
					.getSeqp(), vTexto);

			// Informações complementares
			if (altaTriagem.getDescComplemento() != null) {
				vTexto.append("Inf. Compl.: ")
						.append(altaTriagem.getDescComplemento())
						.append("<br/><br/>");
			}

			processarAlergiasDescricaoTriagem(altaSumarioId, altaTriagem
					.getId().getSeqp(), vTexto);

			// Responsável pela triagem
			vTexto.append(BR);
			vTexto.append(altaTriagem.getResponsavel()).append(BR);
			vTexto.append(altaTriagem.getRegistroResponsavel());
			vTexto.append(BR);
			
			if (!("S").equals(pRelatorio)) {
				vTexto.append(BR + BR);
			}
		}
		
		return vTexto.toString();
	}

	/**
	 * @ORADB MAMC_GET_TIT_DOC_ALT
	 */
	public String montarTituloSumarioAltaAtendEmergencia(
			MpmAltaSumarioId altaSumarioId) {
		String titulo = "";
		Long rgtSeq = getPrescricaoMedicaFacade()
				.obterAltaSumario(altaSumarioId).getRegistro().getSeq();
		MamRegistro registro = getAmbulatorioFacade()
				.obterMamRegistroPorChavePrimaria(rgtSeq);

		if (DominioTipoFormularioAlta.S
				.equals(registro.getTipoFormularioAlta())) {
			titulo = "Boletim de Atendimento";
		} else {
			AghAtendimentos atendimento = getAghuFacade()
					.obterAghAtendimentoPorChavePrimaria(
							altaSumarioId.getApaAtdSeq());
			if (DominioOrigemAtendimento.I.equals(atendimento.getOrigem())) {
				titulo = "Sumário de Alta da Emergência";
			} else if (DominioOrigemAtendimento.U.equals(atendimento
					.getOrigem())) {
				titulo = "Sumário de Atendimento da Emergência";
			}
		}

		return titulo;
	}

	/**
	 * @ORADB MAMC_EMG_VIS_ALT_EXM
	 */
	public String montarExamesRealizados(Integer asuApaAtdSeq,
			Integer asuApaSeq, Short asuSeqp, Boolean relatorio) {
		String retorno = "";
		String comResultado = "";
		String semResultado = "";

		if (!relatorio) {
			retorno = "Exames Realizados\n";
		}

		List<MpmAltaPrincExame> altaPrincExames = getPrescricaoMedicaFacade()
				.listarMpmAltaPrincExamePorIdSemSeqpIndImprime(asuApaAtdSeq,
						asuApaSeq, asuSeqp);
		for (MpmAltaPrincExame mpmAltaPrincExame : altaPrincExames) {
			if (mpmAltaPrincExame.getDthrLiberacao() == null) {
				semResultado = semResultado.concat(
						mpmAltaPrincExame.getDescExame()).concat(", ");
			} else {
				comResultado = comResultado.concat(
						mpmAltaPrincExame.getDescExame()).concat(", ");
			}
		}
		if (comResultado != null && !comResultado.isEmpty()) {
			comResultado = comResultado.substring(0, comResultado.length() - 2);
			retorno = retorno.concat("Exames solicitados com laudo em anexo: ")
					.concat(comResultado).concat("\n");
		}
		if (semResultado != null && !semResultado.isEmpty()) {
			if (comResultado != null && !comResultado.isEmpty()) {
				retorno = retorno.concat("\n");
			}
			semResultado = semResultado.substring(0, semResultado.length() - 2);
			retorno = retorno.concat("Exames sem laudo no momento da alta: ")
					.concat(semResultado).concat("\n");
		}
		retorno = retorno.concat("\n");
		return retorno;
	}

	protected void processarAlergiasDescricaoTriagem(
			MpmAltaSumarioId altaSumarioId, Integer altTriagemSeqp,
			StringBuilder vTexto) {
		Boolean vTitulo;
		// Alergias
		// Busca as alergias da triagem - c_agg
		List<MpmAltaTrgAlergia> alergias = getPrescricaoMedicaFacade()
				.pesquisarAltaTrgAlergiaPorMpmAltaSumarioIdEAltaTriagemSeqp(
						altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(), altTriagemSeqp);

		vTitulo = true;
		for (MpmAltaTrgAlergia alergia : alergias) {
			if (vTitulo) {
				vTexto.append("Alergias: ");
				vTitulo = false;
			}
			vTexto.append(alergia.getDescAlergia()).append("; ");
		}
		if (vTitulo == false) {
			vTexto.substring(0, vTexto.length() - 2);
			vTexto.append("\n" + "\n");
		}
	}

	protected void processarExamesDescricaoTriagem(
			MpmAltaSumarioId altaSumarioId, Integer altaTriagemSeqp,
			StringBuilder vTexto) {
		Boolean vTitulo;
		// Exames
		// Busca os exames da triagem - c_atx
		List<MpmAltaTrgExame> exames = getPrescricaoMedicaFacade()
				.pesquisarAltaTrgExamePorMpmAltaSumarioIdEAltaTriagemSeqp(
						altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(), altaTriagemSeqp);

		vTitulo = true;
		for (MpmAltaTrgExame exame : exames) {
			if (vTitulo) {
				vTexto.append("Exames: ");
				vTitulo = false;
			}
			vTexto.append(exame.getDescExame()).append("; ");
		}
		if (vTitulo == false) {
			vTexto.substring(0, vTexto.length() - 2);
			vTexto.append("\n" + "\n");
		}
	}

	protected void processarMedicamentosDescricaoTriagem(
			MpmAltaSumarioId altaSumarioId, Integer altaTriagemSeqp,
			StringBuilder vTexto) {
		Boolean vTitulo;
		// Medicamentos
		// Busca os medicamentos da triagem - c_ato
		List<MpmAltaTrgMedicacao> medicamentos = getPrescricaoMedicaFacade()
				.pesquisarAltaTrgMedicacaoPorMpmAltaSumarioIdEAltaTriagemSeqp(
						altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(), altaTriagemSeqp);

		vTitulo = true;
		for (MpmAltaTrgMedicacao medicamento : medicamentos) {
			if (vTitulo) {
				vTexto.append("Medicamentos: ");
				vTitulo = false;
			}
			vTexto.append(medicamento.getDescMedicacao()).append("; ");
		}
		if (vTitulo == false) {
			vTexto.substring(0, vTexto.length() - 2);
			vTexto.append("\n" + "\n");
		}
	}

	protected void processarSinaisVitaisDescricaoTriagem(
			MpmAltaSumarioId altaSumarioId, Integer altaTriagemSeqp,
			StringBuilder vTexto) {
		// Sinais vitais
		// Busca os sinais vitais da triagem- c_asv
		List<MpmAltaTrgSinalVital> sinaisVitais = getPrescricaoMedicaFacade()
				.pesquisarAltaTrgSinalVitalPorMpmAltaSumarioIdEAltaTriagemSeqp(
						altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(), altaTriagemSeqp);

		Boolean vTitulo = true;
		for (MpmAltaTrgSinalVital sinalVital : sinaisVitais) {
			if (vTitulo) {
				vTexto.append("Sinais Vitais: ");
				vTitulo = false;
			}
			vTexto.append(sinalVital.getDescSinalVital()).append("; ");
		}
		if (vTitulo == false) {
			vTexto.substring(0, vTexto.length() - 2);
			vTexto.append("\n" + "\n");
		}
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	/**
	 * @ORADB - mamk_emg_visualiza.MAMC_EMG_VIS_ALT_ATD
	 */

	public String buscarDescricaoAtendimento(MpmAltaSumarioId altaSumarioId,
			Boolean pNoConsultorio, String pRelatorio) {

		StringBuilder vTexto = new StringBuilder(100);
		// Boolean vTitulo = Boolean.FALSE;

		// Busca as informações do atendimento - c_aat
		List<MpmAltaAtendimento> altaAtendimentos = getPrescricaoMedicaFacade()
				.pesquisarAltaAtendimentoPeloMpmAltaSumarioId(altaSumarioId);

		for (MpmAltaAtendimento altaAtendimento : altaAtendimentos) {

			// Título
			if ("N".equals(pRelatorio == null ? "N" : pRelatorio)) {
				vTexto.append("\nAtendimento Médico -----------------\n");
			}

			// Agenda
			String vDthrRegistro = DateUtil.dataToString(
					altaAtendimento.getDthrRegistro(), "dd/MM/yyyy HH:mm");
			vTexto.append('\n').append(vDthrRegistro).append(" - ").append(' ')
					.append(altaAtendimento.getDescAgenda()).append('\n').append('\n');

			// Motivo
			Boolean vTitulo = Boolean.TRUE;
			// Busca os motivos de atendimento - c_aav
			List<MpmAltaAtendMotivo> altaAtendMotivos = getPrescricaoMedicaFacade()
					.pesquisarAltaAtendMotivoPorMpmAltaSumarioIdEAltaAtendSeqp(
							altaSumarioId, altaAtendimento.getId().getSeqp());

			for (MpmAltaAtendMotivo altaAtendMotivo : altaAtendMotivos) {
				if (vTitulo) {
					vTexto.append("Motivo do Atendimento: ").append('\n');
					vTitulo = false;
				}
				vTexto.append("          ")
						.append(altaAtendMotivo.getDescCid());
				if (altaAtendMotivo.getDescCompl() != null) {
					vTexto.append(" - ").append(altaAtendMotivo.getDescCompl());
				}
				vTexto.append("; ").append('\n');
			}

			if (vTitulo == false) {
				vTexto.substring(0, vTexto.length() - 2);
				vTexto.append('\n');
			}

			// Registros
			vTitulo = Boolean.TRUE;
			// Busca os registros de atendimento - c_aar
			List<MpmAltaAtendRegistro> altaAtendRegistros = getPrescricaoMedicaFacade()
					.pesquisarAltaAtendRegistroPorMpmAltaSumarioIdEAltaAtendSeqp(
							altaSumarioId, altaAtendimento.getId().getSeqp());
			for (MpmAltaAtendRegistro altaAtendRegistro : altaAtendRegistros) {
				vTexto.append(altaAtendRegistro.getTitulo()).append(": \n          ")
						.append(altaAtendRegistro.getDescRegistro())
						.append('\n');
				vTitulo = Boolean.FALSE;
			}

			if (vTitulo == Boolean.FALSE) {
				vTexto.append('\n');
			}

			// Responsável pelo atendimento
			vTexto.append('\n');
			vTexto.append(altaAtendimento.getResponsavel()).append('\n');
			vTexto.append(altaAtendimento.getRegistroResponsavel())
					.append('\n').append('\n');

		}
		if ("".equals(vTexto.toString())) {
			return null;
		}
		return vTexto.toString();
	}

	/**
	 * @throws ApplicationBusinessException
	 * @ORADB - mamk_emg_visualiza.MAMC_EMG_VIS_MED
	 */
	public List<String> buscarDescricaoMedicamento(
			MpmAltaSumarioId altaSumarioId, String pRelatorio)
			throws ApplicationBusinessException {

		List<String> descricaoMedicamento = new ArrayList<String>();
		// Busca a informação dos fármacos - c_med
		List<MpmAltaPrincFarmaco> altaPrincFarmacos = getPrescricaoMedicaFacade()
				.obterMpmAltaPrincFarmaco(altaSumarioId.getApaAtdSeq(),
						altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(),
						false);

		if (altaPrincFarmacos != null && !altaPrincFarmacos.isEmpty()) {
			for (MpmAltaPrincFarmaco altaPrincFarmaco : altaPrincFarmacos) {

				descricaoMedicamento.add(altaPrincFarmaco.getDescMedicamento());

			}
		} else {
			descricaoMedicamento.add("Nada Consta");
		}
		return descricaoMedicamento;
	}

	/**
	 * @throws ApplicationBusinessException
	 * @ORADB - mamk_emg_visualiza.MAMC_EMG_VIS_ALT_ACN
	 */
	public String buscarConsultoria(MpmAltaSumarioId altaSumarioId,
			String pRelatorio) throws ApplicationBusinessException {

		Boolean vTitulo;
		Boolean vAchouResposta;
		StringBuilder vSemResposta = new StringBuilder();
		StringBuilder descricaoConsultoriaTexto = new StringBuilder();

		// Busca consultorias - c_acn
		List<MpmAltaConsultoria> altaConsultorias = getPrescricaoMedicaFacade()
				.obterListaAltaConsultoriaPeloAltaSumarioId(altaSumarioId);
		List<LinhaReportVO> descricaoConsultorias = new ArrayList<LinhaReportVO>();

		for (MpmAltaConsultoria altaConsultoria : altaConsultorias) {

			LinhaReportVO descricaoConsultoria = new LinhaReportVO();

			descricaoConsultoria.setNumero4(altaConsultoria.getId().getSeqp());
			descricaoConsultoria
					.setTexto1(altaConsultoria.getDescConsultoria());
			descricaoConsultoria.setBool(altaConsultoria
					.getIndImprimeResposta());
			if (altaConsultoria.getIndImprimeResposta().booleanValue()) {
				descricaoConsultoria.setTexto2("1");
			} else {
				descricaoConsultoria.setTexto2("2");
			}
			descricaoConsultorias.add(descricaoConsultoria);
		}
		CoreUtil.ordenarLista(descricaoConsultorias, "texto2", Boolean.TRUE);

		vTitulo = Boolean.TRUE;
		for (LinhaReportVO descricaoConsultoria2 : descricaoConsultorias) {
			if (vTitulo) {
				if ("N".equals(pRelatorio)) {
					descricaoConsultoriaTexto.append("Consultorias").append('\n');
				}
				vTitulo = Boolean.FALSE;
			}
			if (descricaoConsultoria2.getBool()) {// ind_imprime_resposta
				vAchouResposta = Boolean.FALSE;
				// Busca as respostas das consultorias - c_rcs
				List<MpmAltaRespostaConsultoria> respostaConsultorias = getPrescricaoMedicaFacade()
						.pesquisarAltaRespostaConsultoriaPorMpmAltaSumarioIdEAltaConsultoriaSeqp(
								altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp(),
								descricaoConsultoria2.getNumero4());
				StringBuilder descricaoRespostaConsultoriaTexto = new StringBuilder();
				for (MpmAltaRespostaConsultoria respostaConsultoria : respostaConsultorias) {
					if (respostaConsultoria.getIndFinalizacao()) {
						vAchouResposta = Boolean.TRUE;
						descricaoRespostaConsultoriaTexto.append(
								respostaConsultoria.getDescResposta()).append('\n');
						descricaoRespostaConsultoriaTexto
								.append("Realizado por ")
								.append(respostaConsultoria.getResponsavel())
								.append(" - ")
								.append(respostaConsultoria
										.getRegistroResponsavel())
								.append(" - em xx/xx/xxxx xx:xx")
								.append('\n');
					}
				}
				if (vAchouResposta) {
					descricaoConsultoriaTexto.append(' ').append(
							descricaoConsultoria2.getTexto1());
					descricaoConsultoriaTexto.append(": ")
							.append(descricaoRespostaConsultoriaTexto)
							.append('\n');
				} else {
					vSemResposta.append(descricaoConsultoria2.getTexto1()).append(", ");
				}
			} else {
				vSemResposta.append(descricaoConsultoria2.getTexto1()).append(", ");
			}
		}
		if (!(vSemResposta.toString().isEmpty())) {
			vSemResposta = new StringBuilder(vSemResposta.substring(0,vSemResposta.toString().length() - 2));
			descricaoConsultoriaTexto.append("Solicitado consultoria para: ").append(
					vSemResposta).append('\n');
		}
		if (vTitulo == Boolean.FALSE) {
			descricaoConsultoriaTexto.append('\n');
		}

		if ("".equals(descricaoConsultoriaTexto.toString().trim())) {
			return null;
		}
		return descricaoConsultoriaTexto.toString();
	}
}
