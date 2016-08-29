package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.RelatorioEscalaCirurgicaON.RelatorioEscalaCirurgicaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaDiagnosticoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEscalaCirurgiaAghuResponsavelVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.paciente.vo.SubRelatorioEscalaCirurgiasOrteseProteseVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe responsável pelas regras da estória #27566 - Escala de Cirurgias (Não existente no AGH)
 * 
 * @author aghu
 * 
 */
@Stateless
public class RelatorioEscalaCirurgiasAghuON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaCirurgiasAghuON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcAgendaDiagnosticoDAO mbcAgendaDiagnosticoDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private RelatorioEscalaCirurgicaON relatorioEscalaCirurgicaON;

	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;

	private static final long serialVersionUID = 7185919975171367167L;
	private static final String TITULO_CIRURGIOES = "Cirurgiões:\n";
	private static final String TITULO_ANESTESISTAS = "Anestesistas:\n";

	/**
	 * Pesquisa principal do relatório de escala de cirurgias
	 * 
	 * @param unidadesFuncional
	 * @param dataCirurgia
	 * @return
	 */
	public List<EscalaCirurgiasVO> pesquisarRelatorioEscalaCirurgiasAghu(final AghUnidadesFuncionais unidadesFuncional, final Date dataCirurgia, final String login, final Integer grupoMat, MbcSalaCirurgica sala)
	throws ApplicationBusinessException {

		List<EscalaCirurgiasVO> resultado = new ArrayList<EscalaCirurgiasVO>();
		
		// C1: Consulta principal
		List<MbcCirurgias> lista = this.getMbcCirurgiasDAO().pesquisarEscalaCirurgica(unidadesFuncional.getSeq(), dataCirurgia, sala);

		if (lista.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioEscalaCirurgicaONExceptionCode.MENSAGEM_RELATORIO_ESCALA_VAZIO);
		}

		for (MbcCirurgias cirurgia : lista) {

			/*
			 * Seta atributos através da CONSULTA PRINCIPAL
			 */

			EscalaCirurgiasVO vo = new EscalaCirurgiasVO();

			vo.setSeq(cirurgia.getSeq().toString());

			MbcSalaCirurgica salaCirurgica = cirurgia.getSalaCirurgica();
			if (salaCirurgica != null) {
				String sciSeqp = StringUtil.adicionaZerosAEsquerda(salaCirurgica.getId().getSeqp(), 2);
				vo.setSciSeqp(sciSeqp);
			} else {
				vo.setSciSeqp("");
			}
			
			// DTHR_INICIO
			vo.setDthrInicioCirg(this.getRelatorioEscalaCirurgicaON().formataDatas(cirurgia.getDataInicioCirurgia(), RelatorioEscalaCirurgicaON.HORA_HHMM));

			// DTHR_FIM
			vo.setDthrFimCirg(this.getRelatorioEscalaCirurgicaON().formataDatas(cirurgia.getDataFimCirurgia(), RelatorioEscalaCirurgicaON.HORA_HHMM));

			vo.setNumeroAgenda(String.valueOf(cirurgia.getNumeroAgenda())); // NRO_AGENDA

			AipPacientes paciente = cirurgia.getPaciente();

			// Chamada para FUNCTION MBCC_LOCAL_AIP_PAC
			vo.setOrigemPacCirg(this.getEscalaCirurgiasON().pesquisaQuarto(paciente.getCodigo())); // ORIGEM_PAC

			String prontuarioFormatado = CoreUtil.formataProntuario(paciente.getProntuario());
			vo.setProntuario(prontuarioFormatado);

			// Nome
			vo.setNome(StringUtils.substring(paciente.getNome(), 0, 40));

			// Idade
			vo.setIdade(this.getRelatorioEscalaCirurgicaON().calcularIdadePaciente(paciente.getDtNascimento()));

			// Portador de GMR
			vo.setPacienteNotifGMR(this.getPesquisaInternacaoFacade().pacienteNotifGMR(paciente.getCodigo()));

			// Convênio
			vo.setConvenio(cirurgia.getConvenioSaude().getDescricao());

			MbcAgendas agenda = cirurgia.getAgenda();

			if (agenda != null) {

				vo.setRegime(this.obterSiglaRegimeEscalaCirurgia(agenda.getRegime()));

				// Lado da cirurgia
				vo.setLadoCirurgia(this.getRelatorioEscalaCirurgicaON().obterLadoCirurgia(agenda.getLadoCirurgia()));

				// Diagnóstico
				MbcAgendaDiagnostico agendaDiagnostico = getMbcAgendaDiagnosticoDAO().obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(agenda.getSeq());

				if (agendaDiagnostico != null) {
					vo.setDiagnostico(this.getRelatorioEscalaCirurgicaON().obterDiagnostico(agendaDiagnostico.getAghCid()));
				}

			}

			/*
			 * Seta atributos através das CONSULTAS SECUNDÁRIAS
			 */

			// C2: Procedimentos
			getRelatorioEscalaCirurgicaON().obterProcedimentoCirurgico(vo, cirurgia);
			
			//para cada procedimento buscar material segundo OPME
			getRelatorioEscalaCirurgicaON().obterMaterialOrteseProteseOPME(vo, agenda, grupoMat);

			// C3: Órtese e prótese e quantidade solicitada
			getRelatorioEscalaCirurgicaON().obterOrteseProtese(vo, cirurgia);
			
			vo.setOrteseProt(this.formatarOrteseProtese(vo)); // Formata ort/prot
			
			// SETA OBSERVACOES DE OPME
			vo.setObservacaoTitle(null);
			if (agenda != null) {
				if(agenda.getRequisicoesOpmes() != null){
					if(agenda.getRequisicoesOpmes().size() > 0){
						if(agenda.getRequisicoesOpmes().get(0).getObservacaoOpme() != null){
							vo.setObservacao(agenda.getRequisicoesOpmes().get(0).getObservacaoOpme());
							vo.setObservacaoTitle("Observações OPMEs:");
						}
					}
				}
			}

			// C4: Equipamentos
			getRelatorioEscalaCirurgicaON().obterEquipamento(vo, cirurgia);

			// C5: Sangue (Componente sanguíneo)
			getRelatorioEscalaCirurgicaON().obterComponenteSanguineo(vo, cirurgia, null);

			// C6: Anestesia
			vo.setTipoAnestesia(getEscalaCirurgiasON().pesquisaTipoAnestesia(cirurgia.getSeq()));

			// C7: Nome do cirurgião / Médicos responsáveis
			this.obterMedicosResponsaveis(vo, cirurgia.getSeq());

			// C8: Anestesista Professor
			this.obterAnestesistaProfessor(vo, cirurgia.getSeq());

			//Exames
			getRelatorioEscalaCirurgicaON().obterDescricaoExame(vo, cirurgia);
			
			// C9: Solicitação Especial
			getRelatorioEscalaCirurgicaON().obterMaterial(vo, cirurgia);

			// Sea a DESCRIÇÃO COMPLETA DO PROCEDIMENTO na escala de cirurgias AGHU
			vo.setProcedimentoCompletoEscala(this.obterProcedimentoCompletoEscala(vo).trim());

			resultado.add(vo);
		}

		Collections.sort(resultado);
		
		// adicionar linhas
		resultado.get(0).setEscalaCirurgiasVO(resultado);
		List<EscalaCirurgiasVO> listRetorno = new ArrayList<EscalaCirurgiasVO>();
		listRetorno.add(resultado.get(0));

		return listRetorno;
	}

	/**
	 * C7: Obtém os médicos responsáveis
	 * 
	 * @param vo
	 * @param crgSeq
	 */
	public void obterMedicosResponsaveis(final EscalaCirurgiasVO vo, final Integer crgSeq) {
		StringBuffer sbMedicosResponsaveis = new StringBuffer();

		// Médicos: MPF (Médico professor), MCO (Médico contratado) e MAX (Médico auxiliar)
		sbMedicosResponsaveis.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.MPF))
		.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.MCO))
		.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.MAX));

		vo.setNomeCirurgiao(TITULO_CIRURGIOES.concat(sbMedicosResponsaveis.toString()));
	}

	/**
	 * C8: Obtém o anestesista professor
	 * 
	 * @param vo
	 * @param crgSeq
	 */
	public void obterAnestesistaProfessor(final EscalaCirurgiasVO vo, final Integer crgSeq) {
		StringBuffer sbAnestesistasProfessor = new StringBuffer();

		// Anestesistas: ANP (Anestesista professor), ANC (Anestesista contratado) e ANR (Anestesista residente)
		sbAnestesistasProfessor.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.ANP))
		.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.ANC))
		.append(this.obterMedicoAnestesistaResponsavelPorFuncao(crgSeq, DominioFuncaoProfissional.ANR));

		if (StringUtils.isNotEmpty(sbAnestesistasProfessor.toString())) {
			vo.setNomeAnp(TITULO_ANESTESISTAS.concat(sbAnestesistasProfessor.toString()));
		}
	}

	/**
	 * Obtém os profissionais responsável através da função
	 * <p>
	 * A pesquisa dos cursores C7 e C8 são equivalentes, logo foram reutilizadas!
	 * <p>
	 * ATENÇÃO: O critério que considera um conjunto de funções foi individualizado. Ao invés de conjuntos será considerado uma função por vez.
	 * <p>
	 * A formatação de alguns campos está sendo realizada no VO. Exemplo: método getDescricaoRelatorio(): String
	 * 
	 * @param crgSeq
	 * @param funcao
	 * @return
	 */
	public String obterMedicoAnestesistaResponsavelPorFuncao(final Integer crgSeq, final DominioFuncaoProfissional funcao) {
		StringBuffer retorno = new StringBuffer();

		List<RelatorioEscalaCirurgiaAghuResponsavelVO> resultado = this.getMbcProfCirurgiasDAO().pesquisarMedicosResponsaveisRelatorioEscalaCirurgiasAghu(crgSeq, funcao);

		Collections.sort(resultado); // ORDENA por VINCODIGO, MATRICULA, NOME ou NOME USUAL

		RelatorioEscalaCirurgiaAghuResponsavelVO medicoVO = null;
		if (!resultado.isEmpty()) {
			medicoVO = resultado.get(0);
			retorno.append(medicoVO.getDescricaoRelatorio().concat("\n"));
		}

		return retorno.toString();
	}

	/*
	 * Métodos utilitários
	 */

	/**
	 * Obtém a descrição completa do procedimento na escala de cirurgias aghu
	 * 
	 * @param regime
	 * @return
	 */
	private String obterProcedimentoCompletoEscala(EscalaCirurgiasVO vo) {
		StringBuffer buffer = new StringBuffer(512);
		String quebra = "\n";

		// Procedimentos
		if (StringUtils.isNotBlank(vo.getDescricaoPci())) {
			buffer.append(vo.getDescricaoPci());
			buffer.append(quebra);
			buffer.append(quebra);
		}

		// Lateralidade
		if (StringUtils.isNotBlank(vo.getLadoCirurgia())) {
			buffer.append(vo.getLadoCirurgia());
			buffer.append(quebra);
			buffer.append(quebra);
		}

		// Material Ort/Prót
		if (StringUtils.isNotBlank(vo.getOrteseProt())) {
			buffer.append(vo.getOrteseProt());
			buffer.append(quebra);
		}

		// Equipamentos
		if (StringUtils.isNotBlank(vo.getEquipamento())) {
			buffer.append(vo.getEquipamento());
			buffer.append(quebra);
		}

		// Res. Hemoterápica
		if (StringUtils.isNotBlank(vo.getSangue())) {
			buffer.append("Res. Hemoterápica: ");
			buffer.append(vo.getSangue());
			buffer.append(quebra);
			buffer.append(quebra);
		}

		// Téc. Anestésica
		if (StringUtils.isNotBlank(vo.getTipoAnestesia())) {
			buffer.append("Téc. Anestésica: ");
			buffer.append(vo.getTipoAnestesia());
			buffer.append(quebra);
			buffer.append(quebra);
		}

		// Exame
		if (StringUtils.isNotBlank(vo.getExame())) {
			buffer.append(vo.getExame());
			buffer.append(quebra);
		}
		
		// Solicitação Especial
		if (StringUtils.isNotBlank(vo.getMaterial())) {
			buffer.append(vo.getMaterial());
		}

		return buffer.toString();
	}

	/**
	 * Obtém a lista de ort/prot formatada
	 * 
	 * @param regime
	 * @return
	 */
	public String formatarOrteseProtese(EscalaCirurgiasVO vo) {
		StringBuffer retorno = new StringBuffer(256);
		for (SubRelatorioEscalaCirurgiasOrteseProteseVO item : vo.getSubRelatorioEscalaCirurgiasOrteseProtese()) {
			if (retorno.length() == 0) {
				retorno.append(item.getOrteseProt().concat("\n"));
				continue;
			}
			retorno.append("                ".concat(item.getOrteseProt()).concat("\n"));
		}
		return retorno.length() == 0 ? null : retorno.toString();
	}

	/**
	 * Obtém a sigla do regime na escala cirúrgica
	 * 
	 * @param regime
	 * @return
	 */
	public String obterSiglaRegimeEscalaCirurgia(DominioRegimeProcedimentoCirurgicoSus regime) {
		switch (regime) {
		case AMBULATORIO:
			return "AMB";
		case HOSPITAL_DIA:
			return "HOSP DIA";
		case INTERNACAO_ATE_72H:
			return "INT 72H";
		case INTERNACAO:
			return "INT";
		default:
			return null;
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return iPesquisaInternacaoFacade;
	}

	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	private EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}

	private RelatorioEscalaCirurgicaON getRelatorioEscalaCirurgicaON() {
		return relatorioEscalaCirurgicaON;
	}

	private MbcAgendaDiagnosticoDAO getMbcAgendaDiagnosticoDAO() {
		return mbcAgendaDiagnosticoDAO;
	}

	private MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

}