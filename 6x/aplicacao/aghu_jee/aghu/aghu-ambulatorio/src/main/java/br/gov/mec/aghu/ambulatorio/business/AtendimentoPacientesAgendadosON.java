package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceitCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicitacaoRetornoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoAtestadoProcessoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoReceitProcessosDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamProcXCidDAO;
import br.gov.mec.aghu.ambulatorio.vo.AacGradeAgendamenConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketDadosVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketRetornoVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorConDadosVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorCurTicketVO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.ambulatorio.vo.MamSolicitacaoRetornoDocumentoImpressoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultoriaAmbulatorialVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioAvaliacaoInterconsulta;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.dominio.DominioTipoNotaAdicional;
import br.gov.mec.aghu.estoque.vo.MaterialMDAFVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamExtratoControlesId;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemAnamnesesId;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoesId;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.model.MamTipoAtestadoProcesso;
import br.gov.mec.aghu.model.MamTipoReceitProcessos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.vo.CursorCurPreVO;
import br.gov.mec.aghu.util.CapitalizeEnum;

/**
 * Classe responsável pelo atendimento dos Pacientes Agendados.
 * 
 * @author georgenes.zapalaglio
 *
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
@Stateless
public class AtendimentoPacientesAgendadosON extends BaseBusiness {

	private static final String ATESTO_PARA_OS_DEVIDOS_FINS_QUE = "Atesto para os devidos fins que ";
	private static final String ASTERISCOS = "***";
	private static final String TRACO_SEPARADOR = " - ";
	private RelatorioConsultoriaAmbulatorialVO cConsultoriaAmbulatorial = new RelatorioConsultoriaAmbulatorialVO();

	@EJB
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB

	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
		
	private static final Log LOG = LogFactory.getLog(AtendimentoPacientesAgendadosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private MamItemReceitCuidadoDAO mamItemReceitCuidadoDAO;
	
	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;
	
	@Inject
	private VMamProcXCidDAO vMamProcXCidDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;
	
	@Inject
	private MamTipoReceitProcessosDAO mamTipoReceitProcessosDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;


	@Inject
	private MamTipoAtestadoProcessoDAO mamTipoAtestadoProcessoDAO;

	@EJB
	private ManterReceituarioON manterReceituarioON;
	
	private static final String VIRGULA_PRONTUARIO = ", prontuário ";

	@Inject
	private MamAtestadosDAO mamAtestadosDAO;

	@Inject
	private MamReceituarioCuidadoDAO mamReceituarioCuidadoDAO;
		
	private ConselhoProfissionalServidorVO conselhoProfissionalServidorVO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamentoConsutlaDAO;
	
	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;
			
	@Inject
	private MamSolicitacaoRetornoDAO mamSolicitacaoRetornoDAO;

	@EJB
	private AtendimentoPacientesAgendadosAuxiliarON atendimentoPacientesAgendadosAuxiliarON;

	@EJB
	private ImprimirGuiaAtendimentoUnimedRN imprimirGuiaAtendimentoUnimedRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6282720270279716751L;
	private static final String NEWLINE = System.getProperty("line.separator");	
	private static final String ESPACO = " ";
	private static final String ESPACO_5 = "     ";
	private static final String ESPACO_6 = "      ";
	private static final String PONTO = ".";

	public enum AtendimentoPacientesAgendadosONExceptionCode implements BusinessExceptionCode {
		MSG_NO_PARAMETRO, MENSAGEM_JA_EXISTE_EVOLUCAO, MENSAGEM_JA_EXISTE_ANAMNESE, MSG_NAO_INFORME_CID_PARA_O_PROCEDIMENTO, MSG_INFORME_CID_PARA_O_PROCEDIMENTO, ERRO_USUARIO_DE_VALIDACAO, MAM_00193, MAM_00946, MAM_00195, MAM_02339;
		
		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	/**
	 * @ORADB: p_popula_tela_atestado
	 * @ORADB: MAMK_SINTAXE_REP.MAMP_DESCR_ATESTADO
	 * 
	 * @param conNumero
	 * @param tipoAtestado
	 * @throws ApplicationBusinessException
	 */
	public String popularTelaAtestado(Integer conNumero, String tipoAtestado) throws ApplicationBusinessException {
		//Integer tipoSeq;
		Short tasSeq = 0;
		String texto = "";
		if (tipoAtestado != null) {
			switch (tipoAtestado.charAt(0)) {
				case 'C' : //tipoSeq = 5;
						tasSeq = 5;
						texto = "Atestado de Comparecimento";
						break;
				case 'A' : //tipoSeq = 6;
						tasSeq = 6;							
						texto = "Atestado de Acompanhamento";
						break;
				case 'M' : //tipoSeq = 7;
						tasSeq = 7;
						texto = "Atestado Médico";
						break;
				case 'R' : //tipoSeq = 8;
						tasSeq = 8;
						texto = "Atestado de Marcação";
						break;
				case 'G' : //tipoSeq = 9;
						tasSeq = 9;
						texto = "Atestado";
						break;
				case 'F' : //tipoSeq = 22;
						tasSeq = 22;
						texto = "Atestado FGTS e PIS/PASEP";
						break;
				case 'N' : //tipoSeq = 20;
						tasSeq = 20;
						texto = "Renovação Receita";
						break;
			}
		}
		List<MamTipoAtestadoProcesso> curProcessos = mamTipoAtestadoProcessoDAO.obterListaTipoAtestadoProcessoPorTipoAtestado(tasSeq);
		List<MamAtestados> curAte = mamAtestadosDAO.obterAtestadoPorNumeroConsultaTipoAtestado(conNumero, tasSeq);
		Integer qtdAte = 0;
		boolean validaProc=false;
		//Verifica se o usuario tem acesso a assinar
		if (curProcessos != null) {
			for (MamTipoAtestadoProcesso r_proc : curProcessos) { 
				validaProc = this.validarProcesso(null, null, r_proc.getId().getRocSeq());
				if (validaProc) {
					break;
				}
			}
		}
		if (curAte != null) {			
			for (MamAtestados rAte : curAte) {
				if (rAte.getIndPendente() != null && !rAte.getIndPendente().equals("V")) {
					if (rAte.getIndPendente().equals("P") || this.verificarPerfilMarcacao() && !validaProc) {
						return null;
					}
				}
				qtdAte = qtdAte + 1;
				if (qtdAte != 1) {
					texto = texto.concat("-").concat(qtdAte.toString());
				}
			}				
		}						
		return texto;
	}

	/**
	 * @ORADB: MAMK_SINTAXE_REP.MAMP_DESCR_ATESTADO
	 * 
	 * @param seq
	 * @return AtestadoVO
	 */
	public AtestadoVO obterDadosAtestado(Long seq) {
		
		String sintaxeProntuario = "";
		AtestadoVO cAtestado = null;
		String layout = "";
		String descricao = "";
		String nomePaciente = "";
		String nomeAcompanhante = "";
		String indMotivoUsoFGTS = "";
		String codigoCid = "";
		String acometidoDe = "";
		String patternData = "dd/MM/yyyy";
		String patternHora = "HH:mm";
		
		
		if (seq != null) {
			cAtestado = mamAtestadosDAO.obterCursorAtestadosPorSequencial(seq);
		}
		
		if (cAtestado != null) {
			if (cAtestado.getProntuario() != null) {
				sintaxeProntuario = cAtestado.getProntuario().toString();
			}
			
			layout = cAtestado.getLayout();
			
			if (cAtestado.getNomePaciente() != null) {
				nomePaciente = cAtestado.getNomePaciente();
			}
			if (cAtestado.getNomeAcompanhante() != null) {
				nomeAcompanhante = cAtestado.getNomeAcompanhante();
			}
			
			indMotivoUsoFGTS = cAtestado.getIndMotivoUsoFGTS();
			
			if (cAtestado.getCodigoCid() != null) {
				codigoCid = cAtestado.getCodigoCid();
			}
			if (cAtestado.getAcometidoDe() != null) {
				acometidoDe = cAtestado.getAcometidoDe();
			}
		}
		
		//Comparecimento
		descricao = obterDescricaoAtestadoComparecimento(seq, sintaxeProntuario, cAtestado, layout, descricao, nomePaciente, patternData, patternHora);
		
		//Acompanhamento
		descricao = obterDescricaoAtestadoAcompanhamento(seq, sintaxeProntuario, cAtestado, layout, descricao, nomePaciente, nomeAcompanhante, patternData, patternHora);
		
		//Atestado Medico
		descricao = obterDescricaoAtestadoMedico(seq, sintaxeProntuario, cAtestado, layout, descricao, nomePaciente, patternData);
		
		descricao = obterDescricaoRenovacaoReceita(cAtestado, layout, descricao);
		
		//Marcacao de Consulta
		descricao = obterDescricaoAtestadoMarcacaoConsulta(seq, sintaxeProntuario, cAtestado, layout, descricao, nomePaciente, patternData);
		
		//Retirada de fgts e pis/pasep
		if (layout.equals("F")) {			
			if (indMotivoUsoFGTS.equals("NM")) { //Portador de Neoplasia Maligna
				descricao = "Atesto que o paciente: "
						.concat(nomePaciente)
						.concat(", prontuário nº ")
						.concat(sintaxeProntuario)
						.concat(" é sintomático para a patologia classificada sob o CID: ")
						.concat(acometidoDe)
						.concat(". De acordo com a Lei Nº 8.922 de 25/07/1994 ")
						.concat(", nos termos do decreto Nº 5.860/2006 ")
						.concat("e resolução nº 1 de 15/10/1996 do Conselho Diretor do Fundo PIS/PASEP, ")
						.concat("tem o direito à movimentação do FGTS(PIS/PASESP).");
				
			} else if (indMotivoUsoFGTS.equals("DT")) { //Portador de Doença Terminal
				descricao = ATESTO_PARA_OS_DEVIDOS_FINS_QUE
						.concat(nomePaciente)
						.concat(VIRGULA_PRONTUARIO)
						.concat(sintaxeProntuario)
						.concat(", em face dos sintomas e do histórico patológico, encontra-se em estágio terminal de vida em razão de doença grave ")
						.concat(" consignada no CID-10: ")
						.concat(codigoCid)
						.concat(", podendo requerer saque do FGTS e PIS/PASEP de acordo ")
						.concat("com o decreto 5860 de 27 de junho de 2006.");
				
			} else if (indMotivoUsoFGTS.equals("PH")) { //Portador de HIV
				descricao = ATESTO_PARA_OS_DEVIDOS_FINS_QUE
						.concat(nomePaciente)
						.concat(VIRGULA_PRONTUARIO)
						.concat(sintaxeProntuario)
						.concat(" é portador(a) da doença sob o CID-10: ")
						.concat(codigoCid)
						.concat(", podendo requerer saque do FGTS e PIS/PASEP de acordo ")
						.concat("com a Lei 8.922 de 25 de julho de 1994 e conforme Resolução n°1, ")
						.concat("de 15 de outubro de 1996.");
			} else if (indMotivoUsoFGTS.equals("XX")) { //Não Informado
				descricao = ATESTO_PARA_OS_DEVIDOS_FINS_QUE
						.concat(nomePaciente)
						.concat(VIRGULA_PRONTUARIO)
						.concat(sintaxeProntuario)
						.concat("é portador da patologia sob o CID-10: ")
						.concat(codigoCid)
						.concat(", podendo requerer o saque do FGTS e PIS/PASEP de acordo ")
						.concat("com a Lei 8.922 de 25 de julho de 1994 e conforme Resolução n°1, ")
						.concat("de 15 de outubro de 1996. ");
			}
		}
		
		if (cAtestado == null) {
			cAtestado = new AtestadoVO();
		}
		
		cAtestado.setMensagem(descricao);
		cAtestado.setTipoAtestado(layout);

		return cAtestado;
	}


	private String obterDescricaoAtestadoMarcacaoConsulta(Long seq,
			String sintaxeProntuario, AtestadoVO cAtestado, String layout,
			String descricao, String nomePaciente, String patternData) {
		String dataMarcacao;
		if (layout.equals("R")) {
			if (seq == null) {
				dataMarcacao = "";
			} else {
				dataMarcacao = this.obterDataFormatada(cAtestado.getDthrCons(), patternData);
				if (dataMarcacao == null) {
					dataMarcacao = "";
				}
			}
			descricao = "Declaro que paciente ".concat(nomePaciente)
					.concat(VIRGULA_PRONTUARIO).concat(sintaxeProntuario)
					.concat(", esteve neste Hospital no dia ")
					.concat(dataMarcacao).concat(" para marcação de consulta.");
		}
		return descricao;
	}


	private String obterDescricaoAtestadoMedico(Long seq,
			String sintaxeProntuario, AtestadoVO cAtestado, String layout,
			String descricao, String nomePaciente, String patternData) {
		String data1;
		String data2;
		if (layout.equals("M")) {
			if (seq == null) {
				data1 = "";
				data2 = "";
			} else {
				data1 = DateUtil.obterDataFormatada(cAtestado.getDataInicial(), patternData);
				if (data1 == null) {
					data1 = "";
				}
				data2 = DateUtil.obterDataFormatada(cAtestado.getDataFinal(), patternData);
				if (data2 == null) {
					data2 = "";
				}
			}
			descricao = "Atesto, para os devidos fins, que o paciente "
					.concat(nomePaciente)
					.concat(VIRGULA_PRONTUARIO)
					.concat(sintaxeProntuario)
					.concat(", encontra-se sob meus cuidados médicos e impossibilitado de exercer suas atividades do dia ")
					.concat(data1).concat(" ao dia ").concat(data2).concat(".");
		}
		return descricao;
	}
	
	private String obterDescricaoRenovacaoReceita(AtestadoVO cAtestado, String layout, String descricao) {
		if (layout.equals("N")) {
				String prontuario = StringUtils.EMPTY;
				if(cAtestado.getProntuario().toString()!=null){
					prontuario = cAtestado.getProntuario().toString();
				}
				
				String mes = "mês";
				
				if(cAtestado.getPeriodo().intValue() > 1){
					mes= "meses";
				}
				
				descricao = ATESTO_PARA_OS_DEVIDOS_FINS_QUE
						.concat(cAtestado.getNomePaciente())
						.concat(VIRGULA_PRONTUARIO)
						.concat(prontuario)
						.concat(" é portador da(s) patologia(s): ").concat(NEWLINE)
						.concat("  ").concat(cAtestado.getCodigoCid()).concat(TRACO_SEPARADOR)
						.concat(cAtestado.getDescricaoCid()).concat(NEWLINE)
						.concat("  Devendo fazer uso dos medicamentos conforme receita pelo período de ").concat(cAtestado.getPeriodo().toString()).concat(ESPACO).concat(mes)
						.concat(NEWLINE).concat(NEWLINE)
						.concat("  ").concat(cAtestado.getObservacao());	
		}
		return descricao;
	}


	private String obterDescricaoAtestadoAcompanhamento(Long seq,
			String sintaxeProntuario, AtestadoVO cAtestado, String layout,
			String descricao, String nomePaciente, String nomeAcompanhante,
			String patternData, String patternHora) {
		String dataConsulta;
		String horaConsultaIni;
		String horaConsultaFim;
		if (layout.equals("A")) {
			if (seq == null) {
				dataConsulta = "";
				horaConsultaIni = "";
				horaConsultaFim = "";
				nomeAcompanhante = "";
			} else {
				dataConsulta = DateUtil.obterDataFormatada(cAtestado.getDthrCons(), patternData);
				if (dataConsulta == null) {
					dataConsulta = "";
				}
				horaConsultaIni = DateUtil.obterDataFormatada(cAtestado.getDataInicial(), patternHora);
				if (horaConsultaIni == null) {
					horaConsultaIni = "";
				}
				horaConsultaFim = DateUtil.obterDataFormatada(cAtestado.getDataFinal(), patternHora);
				if (horaConsultaFim == null) {
					horaConsultaFim = "";
				}
			}			
			descricao = "Declaro que ".concat(nomeAcompanhante)
					.concat(", esteve neste hospital no dia ")
					.concat(dataConsulta).concat(" das ")
					.concat(horaConsultaIni).concat(" hs às ")
					.concat(horaConsultaFim)
					.concat(" hs, como acompanhante do paciente ")
					.concat(nomePaciente).concat(", prontuário ")
					.concat(sintaxeProntuario)
					.concat(", para atendimento de consulta.");
			
		} else if (layout.equals("O")) {
			if (seq == null) {
				dataConsulta = "";
				horaConsultaIni = "";
				horaConsultaFim = "";
				nomeAcompanhante = "";
			} else {
				dataConsulta = DateUtil.obterDataFormatada(cAtestado.getDataInicial(), patternData);
				horaConsultaIni = DateUtil.obterDataFormatada(cAtestado.getDataInicial(), patternHora);
				horaConsultaFim = DateUtil.obterDataFormatada(cAtestado.getDataFinal(), patternHora);
			}
			descricao = "Declaro que ".concat(nomeAcompanhante)
					.concat(", esteve neste hospital no dia ")
					.concat(dataConsulta).concat(" das ")
					.concat(horaConsultaIni).concat(" hs às ")
					.concat(horaConsultaFim)
					.concat(" hs, como acompanhante do paciente ")
					.concat(nomePaciente).concat(", prontuário ")
					.concat(sintaxeProntuario)
					.concat(", durante sua internação hospitalar.");
		}
		return descricao;
	}


	private String obterDescricaoAtestadoComparecimento(Long seq,
			String sintaxeProntuario, AtestadoVO cAtestado, String layout,
			String descricao, String nomePaciente, String patternData,
			String patternHora) {		
		String dataConsulta;
		String horaConsultaIni;
		String horaConsultaFim;
		if (layout.equals("C")) {
			if (seq == null) {
				dataConsulta = "";
				horaConsultaIni = "";
				horaConsultaFim = "";
			} else {
				dataConsulta = DateUtil.obterDataFormatada(cAtestado.getDthrCons(), patternData);
				if (dataConsulta == null) {
					dataConsulta = "";
				}
				horaConsultaIni = DateUtil.obterDataFormatada(cAtestado.getDataInicial(), patternHora);
				if (horaConsultaIni == null) {
					horaConsultaIni = "";
				}
				horaConsultaFim = DateUtil.obterDataFormatada(cAtestado.getDataFinal(), patternHora);
				if (horaConsultaFim == null) {
					horaConsultaFim = "";
				}
			}			
			descricao = "Declaro que ".concat(nomePaciente)
					.concat(VIRGULA_PRONTUARIO).concat(sintaxeProntuario)
					.concat(", consultou neste Hospital no dia ")
					.concat(dataConsulta).concat(", das ")
					.concat(horaConsultaIni).concat(" hs às ")
					.concat(horaConsultaFim).concat(" hs.");
			
		} else if (layout.equals("P")) {
			if (seq == null) {
				dataConsulta = "";
			} else {
				dataConsulta = DateUtil.obterDataFormatada(cAtestado.getDthrCons(), patternData);
				if (dataConsulta == null) {
					dataConsulta = "";
				}
			}			
			descricao = "Declaro que o paciente ".concat(nomePaciente)
					.concat(VIRGULA_PRONTUARIO).concat(sintaxeProntuario)
					.concat(", está internado nesta instituição desde o dia ")
					.concat(dataConsulta).concat(".");
		}
		return descricao;
	}
	
	
	
	/**
	 *  
	 * @param seq
	 * @return ReceituarioCuidadoVO
	 */
	public List<MamReceituarioCuidadoVO> obterDadosReceituarioCuidaddo(Long seq, Integer conNumero, Short espSeq) {

		List<MamReceituarioCuidadoVO> rCuidado = null;
		List<MamReceituarioCuidadoVO> listaRegistro = null;
		String nomePaciente = "";

			rCuidado = mamItemReceitCuidadoDAO.obterNomeEDescricaoReceita(seq);
			listaRegistro = mamReceituarioCuidadoDAO.pesquisarRegistro(seq);


		if (rCuidado != null) {
			for (MamReceituarioCuidadoVO receituarioCuidado : rCuidado) {

				nomePaciente = mamItemReceitCuidadoDAO.obterNomeReceita(seq);
				List<MaterialMDAFVO> descricaoReceita = mamItemReceitCuidadoDAO.obterDescricaoReceita(seq, nomePaciente);
				if(!descricaoReceita.isEmpty()){							
					receituarioCuidado.setDescricao(descricaoReceita);
				}
				receituarioCuidado.setNome(nomePaciente);
				receituarioCuidado.setSeq(seq);
				
				if(!listaRegistro.isEmpty()){
				for(MamReceituarioCuidadoVO rCuidadoVO: listaRegistro){
						receituarioCuidado.setNroVias(rCuidadoVO.getNroVias());
					}
				}
				if(espSeq != null){
					receituarioCuidado.setEspecialidade(manterReceituarioON.obterEspecialidade(espSeq));
				}
			}
				return rCuidado;
			}
		return null;
	}
	
	
	/**
	 * Formata data
	 * @param data
	 * @param pattern
	 * @return Data Formatada
	 */
	private String obterDataFormatada(Date data, String pattern) {		
		String dataFormatada = DateUtil.obterDataFormatada(data, pattern);		
		if (dataFormatada == null) {
			dataFormatada = " ";
		}		
		return dataFormatada;
	}
	
//	/**
//	 * Formata data
//	 * @param data
//	 * @param pattern
//	 * @return Data Formatada
//	 */
//	private String obterDataFormatada(Date data, String pattern) {
//		String dataFormatada = DateUtil.obterDataFormatada(data, pattern);
//		if (dataFormatada == null) {
//			dataFormatada = " ";
//		}
//		return dataFormatada;
//	}
	
	/**
	 * Verifica se o paciente tem documentos a serem impressos.
	 * 
	 * ORADB: Package MAMK_GENERICA.MAMC_VER_DOC_IMP
	 * 
	 * @param consulta
	 *  
	*/
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Boolean verificarImpressaoDocumentosPaciente(AacConsultas consulta, Boolean verificarProcesso) throws ApplicationBusinessException {
		Boolean vTemImp = false;
		Boolean vTemExame;
		Boolean vTemExamePendente;
		
		// Busca parametro que indica se a Anamnese deve ser impressa
		String vAnamnese = retornarParametroImprimeAnamnese();
		
		// Busca parametro que indica se a Evolução deve ser impressa
		String vEvolucao = retornarParametroImprimeEvolucao();	

		// Parametro que indica qual é a Situacao para exame cancelado
		String vSituacaoCancelado = retornarCodigoExameSituacaoCancelado();
		 
		// Parametro que indica qual é a situacao para exame pendente
		String vSituacaoPendente = retornarCodigoExameSituacaoPendente();

		Boolean vProntoAtendimento = false;
		
		if(consulta.getGradeAgendamenConsulta().getEspecialidade()!=null){
			List<AghCaractEspecialidades> listaCaractEspecialidade = this.aghuFacade.pesquisarCaracteristicaEspecialidadePorEspecialidade(
					consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq());
			if(listaCaractEspecialidade!=null){
				for (AghCaractEspecialidades caract : listaCaractEspecialidade){
					if (caract != null && caract.getId() != null &&  caract.getId().getCaracteristica() != null && DominioCaracEspecialidade.EMERGENCIA.equals(caract.getId().getCaracteristica())){
						vProntoAtendimento = true;
					}
				}	
			}
		}
		
		/* Anamnese
		   Se o sistema deve imprimir a anamnese elaborada ou a consulta é do
		   pronto atendimento, então deve-se verificar a existência da anamnese e se
		   existir deverá retornar que há documentos a serem impressos.
		*/
		if(vAnamnese.equalsIgnoreCase("S") || vProntoAtendimento) {
			List<MamAnamneses> listaAnamneses = mamAnamnesesDAO.obterAnamnesesPorNumeroConsultaEIndPendente(consulta.getNumero());
			if(!listaAnamneses.isEmpty() && (!verificarProcesso || validarProcesso(null,null,this.retornarCodigoProcessoAnamnese()))) {
					return vTemImp = true;
			}	
		}	   
		   
		/* Evolucao
		   Se o sistema deve imprimir a evolução elaborada ou a consulta é do
		   pronto atendimento, então deve-se verificar a existência da evolução e se
		   existir deverá retornar que há documentos a serem impressos.
		*/
		if(vEvolucao.equalsIgnoreCase("S") || vProntoAtendimento) {
			List<MamEvolucoes> listaEvolucoes = mamEvolucoesDAO.obterEvolucoesPorNumeroConsultaEIndPendente(consulta.getNumero());
			if(!listaEvolucoes.isEmpty() && (!verificarProcesso || validarProcesso(null,null,this.retornarCodigoProcessoEvolucao()))) {
					return vTemImp = true;
			}	
		}
		
		// Receituários
		List<MamReceituarios> listaReceitas = mamReceituariosDAO.obterReceituariosPorNumeroConsultaEIndPendente(consulta.getNumero());
		if(!listaReceitas.isEmpty() && (!verificarProcesso || validarProcesso(null,null,this.retornarCodigoProcessoReceita()))) {
				return vTemImp = true;
		}	
		
		// Exames									não comitar assim nao comitar assim não comitar assim nao comitar assim
		if(this.retornarImpressaoExames(consulta.getNumero())) {
			vTemExame = true;
			vTemExamePendente = false;
			
			List<AelItemSolicitacaoExames> listaExames = examesFacade.obterSitCodigoPorNumeroConsultaESituacao(consulta.getNumero(), vSituacaoCancelado);
			for(AelItemSolicitacaoExames exame : listaExames) {
				if(exame.getSituacaoItemSolicitacao().getCodigo().equalsIgnoreCase(vSituacaoPendente)) {
					vTemExamePendente = true;
				}
			}	
			if(vTemExame) {
				if(vTemExamePendente && (!verificarProcesso || validarProcesso(null,null,this.retornarCodigoProcessoExame()))){
					return vTemImp = true; 
				} else if(!vTemExamePendente){
					return vTemImp = true;
				}
			}
		}
		
		//Tratamento Fisiátrico
		vTemImp = atendimentoPacientesAgendadosAuxiliarON.verificarImpressaoTratamentoFisiatrico(consulta, vTemImp);
		
		//Atestados
		vTemImp = verificarImpressaoAtestado(consulta, vTemImp, verificarProcesso);
		
//		//Receituario Cuidado
		vTemImp = verificarReceituarioCuidado(consulta, vTemImp, verificarProcesso);
//			
//		vTemImp = verificarSolicitacaoRetornoDocumentoImpresso(consulta, vTemImp);
			
		vTemImp = verificarSolicitacaoRetornoDocumentoImpresso(consulta, vTemImp, verificarProcesso);
		
		vTemImp = atendimentoPacientesAgendadosAuxiliarON.verificarSolicHemoterapia(consulta,vTemImp, verificarProcesso);
		
		vTemImp = atendimentoPacientesAgendadosAuxiliarON.verificarImpressaoDocumentosPaciente(consulta, vTemImp, verificarProcesso);
		
		vTemImp = verificarInterconsultas(consulta,vTemImp, verificarProcesso);
				
		vTemImp = imprimirGuiaAtendimentoUnimedRN.verificarConvenioUnimed(consulta.getNumero(), vTemImp);
		
		vTemImp = verificarRenovacaoReceita(consulta, vTemImp, verificarProcesso); //ON06 #45902
				
		return vTemImp;
	}

	private Boolean verificarImpressaoAtestado(AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException {
		List<MamAtestados> rAte = mamAtestadosDAO.obterAtestadoPorNumeroConsulta(consulta.getNumero());
		
		if (rAte != null) {
			for (MamAtestados atestado : rAte) {
				if (atestado.getIndPendente() != null && DominioIndPendenteAmbulatorio.V.equals(atestado.getIndPendente())) {
					vTemImp = true;
					return vTemImp;
				} else if (atestado.getIndPendente() != null && DominioIndPendenteAmbulatorio.P.equals(atestado.getIndPendente())) {
					List<MamTipoAtestadoProcesso> rProc = mamTipoAtestadoProcessoDAO.obterListaTipoAtestadoProcessoPorTipoAtestado(atestado.getMamTipoAtestado().getSeq());
					if (rProc != null) {
						for (MamTipoAtestadoProcesso tipoAtestadoProcesso : rProc) {
							if (!verificarProcesso || this.validarProcesso(null, null, tipoAtestadoProcesso.getId().getRocSeq())) {
								vTemImp = true;
								return vTemImp;
							}
						}
					}				
				} else {
					vTemImp = true;
					return vTemImp;
				}
			}
		}
		return vTemImp;
	}
		
	private Boolean verificarReceituarioCuidado(AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException {
		Short paramReceitaTipoCuidado = retornarCodigoReceitaTipoCuidado();
		List<MamReceituarioCuidado> listaReceitaCuidado = mamReceituarioCuidadoDAO.pesquisarDocumentoImpresso(consulta.getNumero());
		List<MamTipoReceitProcessos> listaTipoReceita = mamTipoReceitProcessosDAO.listarMamTipoReceitProcessosCodigo(paramReceitaTipoCuidado);

		for (MamReceituarioCuidado documento : listaReceitaCuidado){
			if (documento.getPendente() != null) {
			if(documento.getPendente() == DominioIndPendenteAmbulatorio.V){
				return vTemImp = true;
				} else if (documento.getPendente() == DominioIndPendenteAmbulatorio.P) {
				
				for (MamTipoReceitProcessos tipoReceita : listaTipoReceita){
					if(!verificarProcesso || validarProcesso(null, null, tipoReceita.getId().getRocSeq()) == true){
						return vTemImp = true;
					}
				}
			}
		}
		}
		return vTemImp;
	}

	/**
	 * ON06 - 45902
	 * @param consulta
	 * @param vTemImp
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	private Boolean verificarRenovacaoReceita(AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException {
		List<MamAtestados> listaAtestados = mamAtestadosDAO.obterAtestadoPorNumeroConsultaDthrValidaNula(consulta.getNumero());
		for (MamAtestados mamAtestados : listaAtestados) {
			if(DominioIndPendenteAmbulatorio.V.equals(mamAtestados.getIndPendente()) ){
				vTemImp = true;
				return vTemImp;
			}else if(DominioIndPendenteAmbulatorio.P.equals(mamAtestados.getIndPendente()) ){
				List<MamTipoAtestadoProcesso> listaTipoAtestados = mamTipoAtestadoProcessoDAO.obterListaTipoAtestadoProcessoPorTipoAtestado(mamAtestados.getMamTipoAtestado().getSeq());
				for (MamTipoAtestadoProcesso mamTipoAtestadoProcesso : listaTipoAtestados) {
					if(!verificarProcesso ||validarProcesso(null, null, mamTipoAtestadoProcesso.getId().getRocSeq())){
						vTemImp = true;
						return vTemImp;
					}
				}
			}
		}
		return vTemImp;
	}
	
	public Boolean existeDocumentosImprimirPaciente(AacConsultas consulta, Boolean verificarProcesso) throws ApplicationBusinessException {
		//this.verificarImpressaoDocumentosPaciente(consulta);
		if(!this.verificarImpressaoDocumentosPaciente(consulta, verificarProcesso)) {
			throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.MAM_02339);
		}		
		return true;
	}
	
	/**
	 * Verifica se o usuário pode ou não validar um determinado processo
	 * 
	 * ORADB: Package MAMK_PERFIL.MAMC_VALIDA_PROCESSO
	 * 
	 * @param pSerVinCodigo, pSerMatricula, pSeqProcesso
	 *  
	*/
	public Boolean validarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pSeqProcesso) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		String login=null;
		if(pSerVinCodigo != null && pSerMatricula != null) {
			RapServidores servidor = registroColaboradorFacade.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(pSerMatricula, pSerVinCodigo);
			if (servidor.getUsuario()!=null) {
			login = servidor.getUsuario().toUpperCase();
		}
		}
		if (login ==null){			
			login = servidorLogado.getUsuario();
		}
		
		if(cascaFacade.validarPermissaoPorServidorESeqProcesso(login, pSeqProcesso)){
			return true;
		}
		
		return false;
	}
	
	
	public Boolean validarProcessoConsultaAnamnese() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoAnamnese();
		return this.validarProcessoConsulta(seqProcesso);
	}
	
	public Boolean validarProcessoConsultaEvolucao() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoEvolucao();
		return this.validarProcessoConsulta(seqProcesso);
	}
	
	public Boolean validarProcessoConsultaReceita() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoReceita();
		return this.validarProcessoConsulta(seqProcesso);
	}
	
	public Boolean validarProcessoConsultaProcedimento() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoRelatorio();
		return this.validarProcessoConsulta(seqProcesso);
	}
	
	public Boolean validarProcessoConsultaAtestado() throws ApplicationBusinessException {
		Short seqProcesso = this.retornarCodigoProcessoAtestado();
		return this.validarProcessoConsulta(seqProcesso);
	}
	
	public Boolean validarProcessoExecutaSolicitacaoExame() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoExame();
		return this.validarProcessoExecuta(seqProcesso);
	}
	
	public Boolean validarProcessoExecutaAnamnese() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoAnamnese();
		return this.validarProcessoExecuta(seqProcesso);
	}
	
	public Boolean validarProcessoExecutaEvolucao() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoEvolucao();
		return this.validarProcessoExecuta(seqProcesso);
	}
	
	public Boolean validarProcessoExecutaReceita() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoReceita();
		return this.validarProcessoExecuta(seqProcesso);
	}
	
	public Boolean validarProcessoExecutaProcedimento() throws ApplicationBusinessException{
		Short seqProcesso = this.retornarCodigoProcessoRelatorio();
		return this.validarProcessoExecuta(seqProcesso);
	}
	
	public Boolean validarProcessoConsulta(Short pSeqProcesso) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if(cascaFacade.validarPermissaoConsultaPorServidorESeqProcesso(servidorLogado.getUsuario(), pSeqProcesso)){
			return true;
		}
		return false;
	}
	
	public Boolean validarProcessoExecuta(Short pSeqProcesso) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if(cascaFacade.validarPermissaoExecutaPorServidorESeqProcesso(servidorLogado.getUsuario(), pSeqProcesso)){
			return true;
		}
		return false;
	}
	
	/**
	 * Retorna o código do processo que representa a anamnese 
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ANAM
	 *  
	*/
	public Short retornarCodigoProcessoAnamnese() throws ApplicationBusinessException {
		Short codigoProcessoAnamnese = 0;
		AghParametros parametroCodProcessoAnamnsese = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_ANAMNESE);
		if(parametroCodProcessoAnamnsese.getVlrNumerico()!=null){
			codigoProcessoAnamnese = parametroCodProcessoAnamnsese.getVlrNumerico().shortValue();	
		}
		return codigoProcessoAnamnese;
	}
	
	/**
	 * Retorno o código do processo que representa a evolução 
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_EVO
	 *   
	*/
	public Short retornarCodigoProcessoEvolucao() throws ApplicationBusinessException {
		Short codigoProcessoEvolucao = 0;
		AghParametros parametroCodProcessoEvolucao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_EVOLUCAO);
		if(parametroCodProcessoEvolucao.getVlrNumerico()!=null){
			codigoProcessoEvolucao = parametroCodProcessoEvolucao.getVlrNumerico().shortValue();	
		}
		return codigoProcessoEvolucao;
	}
	
	/**
	 * Retorno o código do processo que representa a receita
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_RECEIT
	 *   
	*/
	public Short retornarCodigoProcessoReceita() throws ApplicationBusinessException {
		Short codigoProcessoReceita = 0;
		AghParametros parametroCodProcessoReceita = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_RECEITA);
		if(parametroCodProcessoReceita.getVlrNumerico()!=null){
			codigoProcessoReceita = parametroCodProcessoReceita.getVlrNumerico().shortValue();	
		}
		return codigoProcessoReceita;
	}
	
	/**
	 * Retorno o código do processo que representa o exame
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_EXA
	 *   
	*/
	public Short retornarCodigoProcessoExame() throws ApplicationBusinessException {
		Short codigoProcessoExame = 0;
		AghParametros parametroCodProcessoExame = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_SOLIC_EXAMES);
		if(parametroCodProcessoExame.getVlrNumerico()!=null){
			codigoProcessoExame = parametroCodProcessoExame.getVlrNumerico().shortValue();	
		}
		return codigoProcessoExame;
	}
	
	
	/**
	 * Retorno o código do processo que representa o relatório
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_POL
	 *   
	*/
	public Short retornarCodigoProcessoRelatorio() throws ApplicationBusinessException {
		Short codigoProcessoRelatorio = 0;
		AghParametros parametroCodProcessoRelatorio = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_POL);
		if(parametroCodProcessoRelatorio.getVlrNumerico()!=null){
			codigoProcessoRelatorio = parametroCodProcessoRelatorio.getVlrNumerico().shortValue();	
		}
		return codigoProcessoRelatorio;
	}
//	
//	/**
//	 * Retorno o código do processo que representa o relatório
//
//	 *  
//	*/
//
//	 * 
//	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_LAUDO
//	 *   
//	*/
//	public Short retornarCodigoProcessoLaudo() throws ApplicationBusinessException {
//		Short codigoProcessoLaudo = 0;
//		AghParametros parametroCodProcessoLaudo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_LAUDO_AIH);
//		if(parametroCodProcessoLaudo.getVlrNumerico()!=null){
//			codigoProcessoLaudo = parametroCodProcessoLaudo.getVlrNumerico().shortValue();	
//		}
//		return codigoProcessoLaudo;
//	}
	
	/**
	 * Retorno o código do processo que representa o relatório
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_LAUDO
	 *   
	*/
	public Short retornarCodigoProcessoLaudo() throws ApplicationBusinessException {
		Short codigoProcessoLaudo = 0;
		AghParametros parametroCodProcessoLaudo = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_LAUDO_AIH);
		if(parametroCodProcessoLaudo.getVlrNumerico()!=null){
			codigoProcessoLaudo = parametroCodProcessoLaudo.getVlrNumerico().shortValue();	
		}
		return codigoProcessoLaudo;
	}
	
	/**
	 * Retorno o código do processo que representa o relatório
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ALTA
	 *   
	 */
	public Short retornarCodigoProcessoAlta() throws ApplicationBusinessException {
		Short codigoProcessoAlta = 0;
		AghParametros parametroCodProcessoAlta = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_ALTA);
		if(parametroCodProcessoAlta.getVlrNumerico()!=null){
			codigoProcessoAlta = parametroCodProcessoAlta.getVlrNumerico().shortValue();	
		}
		return codigoProcessoAlta;
	}
	
	public Short retornarCodigoProcessoAtestado() throws ApplicationBusinessException {
		Short codigoProcessoAtestado = 0;
		AghParametros parametrosCodProcessoAtestado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_ATESTADO);
		if(parametrosCodProcessoAtestado.getVlrNumerico() != null) {
			codigoProcessoAtestado = parametrosCodProcessoAtestado.getVlrNumerico().shortValue();
		}
		return codigoProcessoAtestado;
	}
	
	/**
	 * ORADB: Function MAMC_GET_IMP_EXAMES
	 *  
	*/
	public boolean retornarImpressaoExames(Integer conNumero) throws ApplicationBusinessException {
		// Parametro que indica qual é a Situacao para exame cancelado
		String vSituacaoCancelado = retornarCodigoExameSituacaoCancelado();
		 
		// Parametro que indica qual é a Situacao para exame pendente
		String vSituacaoPendente = retornarCodigoExameSituacaoPendente();
		
		Integer vTotal = 0;
		List<AelSolicitacaoExames> listaSolicitacaoExames = examesFacade.obterSolicitacoesExamesPorConsultaESituacao(conNumero, vSituacaoPendente, vSituacaoCancelado);
		if(listaSolicitacaoExames!=null && !listaSolicitacaoExames.isEmpty()) {
			for(AelSolicitacaoExames exame : listaSolicitacaoExames) {
				if((internacaoFacade.verificarCaracteristicaUnidadeFuncional(exame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)==false)){
					vTotal++;
				}
			}
		}
		
		if(vTotal>0){
			return true;
		}
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExames = examesFacade.obterItemSolicitacaoExamesPorNumeroConsultaESituacao(conNumero, vSituacaoPendente, vSituacaoCancelado);
		if(listaItemSolicitacaoExames!=null) {
			for(AelItemSolicitacaoExames itemSolicitacaoExame : listaItemSolicitacaoExames) {
				String cExaSigla = itemSolicitacaoExame.getExame().getSigla();
				Integer cManSeq = itemSolicitacaoExame.getMaterialAnalise().getSeq();
				Short cCspCnvCodigo = itemSolicitacaoExame.getSolicitacaoExame().getConvenioSaude().getCodigo();
				DominioOrigemAtendimento cOrigemAtend = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getOrigem();
			
				if (this.examesFacade.verificarExistenciaRespostasExamesQuestionario(cExaSigla, cManSeq, cCspCnvCodigo, cOrigemAtend)
						&& this.examesFacade.verificarRespostaQuestao(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp())) {
						return true;
				}
			}
		}
		
		return false;
	}
	
	public String retornarCodigoExameSituacaoCancelado() throws ApplicationBusinessException {
		// Busca parametro que indica qual é a Situacao para exame cancelado
		String vSituacaoCancelado = null;
		AghParametros parametroSituacaoCancelado = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		if(parametroSituacaoCancelado.getVlrTexto()!=null){
			vSituacaoCancelado = parametroSituacaoCancelado.getVlrTexto();	
		} 
		
		return vSituacaoCancelado;
	}
	
	public String retornarCodigoExameSituacaoPendente() throws ApplicationBusinessException {
		// Busca parametro que indica qual é a situacao para exame pendente
		String vSituacaoPendente = null;
		AghParametros parametroSituacaoPendente = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE);
		if(parametroSituacaoPendente.getVlrTexto()!=null){
			vSituacaoPendente = parametroSituacaoPendente.getVlrTexto();	
		}
		
		return vSituacaoPendente;
	}
	
	public String retornarParametroImprimeAnamnese() throws ApplicationBusinessException {
		// Busca parametro que indica se a Anamnese deve ser impressa
		String vAnamnese = null;
		AghParametros parametroAnamnese = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPRIME_ANAMNESE);
		if(parametroAnamnese.getVlrTexto()!=null){
			vAnamnese = parametroAnamnese.getVlrTexto();	
		} 
		
		return vAnamnese;
	}
	
	public String retornarParametroImprimeEvolucao() throws ApplicationBusinessException {
		// Busca parametro que indica se a Evolução deve ser impressa
		String vEvolucao = null;
		AghParametros parametroEvolucao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPRIME_EVOLUCAO);
		if(parametroEvolucao.getVlrTexto()!=null){
			vEvolucao = parametroEvolucao.getVlrTexto();	
		} 
	
		return vEvolucao;
	}
	
	private MamInterconsultas obterInterconsulta(AacConsultas consulta){
		MamInterconsultas interconsulta = null;
		List<MamInterconsultas> interconsultas = mamInterconsultasDAO.obterInterconsultaPorConsultaSituacao(consulta);
		if (interconsultas!=null && !interconsultas.isEmpty()){
			interconsulta=interconsultas.get(0);
		}
		return interconsulta;
	}
	
	private Boolean verificaConsutoriaValida(MamInterconsultas interconsulta) {
		Boolean retorno = false;
		if (interconsulta != null
				&& (interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.S) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.I) || interconsulta.getAvaliacao()
						.equals(DominioAvaliacaoInterconsulta.C)) && interconsulta.getPendente().equals(DominioIndPendenteAmbulatorio.V) && interconsulta.getDthrValidaMvto() == null
				&& interconsulta.getParecerConsultor() != null) {
			retorno = true;
		}
		return retorno;
	}
	
	private StringBuilder obtemRespostaConsultoria(MamInterconsultas interconsulta) {
		StringBuilder texto = new StringBuilder();
		if (verificaConsutoriaValida(interconsulta)) {
			texto.append("Resposta da solicitação de consultoria ambulatorial para ");
			if (interconsulta.getEspecialidade() != null) {
				texto.append(interconsulta.getEspecialidade().getNomeEspecialidade());
			}
			if (interconsulta.getEspecialidadeAgenda() != null && interconsulta.getEspecialidadeAgenda().getNomeEspecialidade().equals(interconsulta.getEspecialidade().getNomeEspecialidade())) {
				texto.append(TRACO_SEPARADOR).append(interconsulta.getEspecialidadeAgenda().getNomeEspecialidade());
			}
			if (interconsulta.getEquipe() != null) {
				texto.append(" Equipe ").append(interconsulta.getEquipe().getNome());
			}
			texto.append(" solicitada em ").append(DateUtil.obterDataFormatada(interconsulta.getDthrCriacao(), "dd/MM/yyyy HH:mm")).append(NEWLINE).append("Avaliação: ")
					.append(interconsulta.getAvaliacao().getDescricao()).append(NEWLINE).append("Parecer: ");
			if (interconsulta.getParecerConsultor() != null) {
				texto.append(interconsulta.getParecerConsultor());
			}
		}
		return texto;
	}
	
	/**
	 * Obtem a lista de documentos a serem impressos
	 * @param conNumero
	 *  
	*/
	@SuppressWarnings("PMD.NPathComplexity")
	public List<DocumentosPacienteVO> obterListaDocumentosPaciente(Integer conNumero, Integer gradeSeq, Boolean verificarProcesso) throws ApplicationBusinessException {
		List<DocumentosPacienteVO> listaDocumentos = new ArrayList<DocumentosPacienteVO>();
		
		// Busca parametro que indica qual eh o processo da solicitacao de exames
		Short paramSolicitacaoExames = retornarCodigoProcessoExame();
		
		// Parametro que indica qual eh a Situacao para exame cancelado
		String vSituacaoCancelado = retornarCodigoExameSituacaoCancelado();
		 
		// Parametro que indica qual eh a situacao para exame pendente
		String vSituacaoPendente = retornarCodigoExameSituacaoPendente();
		
		// Ticket Exame
		if((!verificarProcesso || this.validarProcesso(null, null, paramSolicitacaoExames)) && existeTicketExamePaciente(conNumero)) {
			List<Integer> listaSeqSolicitacaoExames = examesFacade.obterSeqSolicitacoesExamesPorConsulta(conNumero, vSituacaoPendente, vSituacaoCancelado);
			for(Integer seqSolicitacaoExames : listaSeqSolicitacaoExames) {
				AelSolicitacaoExames exame = examesFacade.obterAelSolicitacaoExamePorChavePrimaria(seqSolicitacaoExames);
				if((internacaoFacade.verificarCaracteristicaUnidadeFuncional(exame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)==false)) {
					String descricaoExame = "";
					Boolean existeImpresso = false;
					for(AelItemSolicitacaoExames ticketExame : exame.getItensSolicitacaoExame()) {
						if(ticketExame.getIndImprimiuTicket()==true){
							existeImpresso = true;
						}
						if(!descricaoExame.equalsIgnoreCase("")){
							descricaoExame = descricaoExame.concat(", ");
						}
					    descricaoExame = descricaoExame.concat(ticketExame.getExame().getDescricao());
					}	
					DocumentosPacienteVO documentoSolicitacaoExame = setarDadosDocumentoPaciente("Ticket ao Paciente - " + exame.getSeq() + TRACO_SEPARADOR + descricaoExame, existeImpresso);
					documentoSolicitacaoExame.setSolicitacaoExame(exame);
					listaDocumentos.add(documentoSolicitacaoExame);
				} 
			}
		}
		
		// Receituario	
		// Busca parametro tipo de receituario que corresponde ao receituario de cuidados
		Short cReceituarioCuidados = null;
		AghParametros parametroReceituarioCuidados = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TP_REC_CUIDADOS);
		if(parametroReceituarioCuidados.getVlrNumerico()!=null){
			cReceituarioCuidados = parametroReceituarioCuidados.getVlrNumerico().shortValue();	
		} 
		Boolean vValidaProc = false;
		List <MamTipoReceitProcessos> listaReceitProcessos = mamTipoReceitProcessosDAO.listarMamTipoReceitProcessosPorCodigoReceituarioCuidados(cReceituarioCuidados);
		for(MamTipoReceitProcessos receitProcesso : listaReceitProcessos) {
			if(!verificarProcesso || validarProcesso(null, null, receitProcesso.getId().getRocSeq())){
					vValidaProc = true;
			}
		}
		
		Integer qtdReceituarios = 1;
		List<MamReceituarios> receituarios = mamReceituariosDAO.obterReceituariosPorNumeroConsultaEIndPendente(conNumero);
		for(MamReceituarios receituario : receituarios) {
			if(receituario.getPendente()==DominioIndPendenteAmbulatorio.V && vValidaProc 
					|| receituario.getPendente()==DominioIndPendenteAmbulatorio.P && vValidaProc) {
					DocumentosPacienteVO documentoReceituario = setarDadosDocumentoPaciente("Receita "+qtdReceituarios,receituario.getIndImpresso().isSim());
					documentoReceituario.setReceituarios(receituario);
					listaDocumentos.add(documentoReceituario);
					qtdReceituarios++;
			}
		}
		

		AacConsultas consulta = aacConsultasDAO.obterConsulta(conNumero);
		Short espSeq = consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq();
		
		Boolean vProntoAtendimento = false;
		if(consulta.getGradeAgendamenConsulta().getEspecialidade().getCaracteristicas()!=null){
			for (AghCaractEspecialidades caract : consulta.getGradeAgendamenConsulta().getEspecialidade().getCaracteristicas()){
				if (DominioCaracEspecialidade.EMERGENCIA.equals(caract.getId().getCaracteristica())){
					vProntoAtendimento = true;
				}
			}
		}
		
		this.carregaEvolucaoeAnamnese(conNumero, listaDocumentos, vProntoAtendimento, verificarProcesso, espSeq);
		
		// Interconsultas
		obterListaDocumentosInterConsulta(conNumero, listaDocumentos, vProntoAtendimento, espSeq);

		// Questionario
		// List<AelRespostaQuestao> aelRespostaQuestoes =
		// getAelRespostaQuestaoDAO().pesquisarRespostaPorConsultaPendente(conNumero,vSituacaoPendente);
		realizarDocQuestionario(conNumero, listaDocumentos, vSituacaoPendente);
		
		//Tratamento Fisiátrico
		atendimentoPacientesAgendadosAuxiliarON.obterListaDocumentosPacienteTratamentoFisiatrico(conNumero, listaDocumentos);
		
		//Atestados
		//obterListaDocumentosPacienteAtestados(conNumero, listaDocumentos);
		
		List<MamAtestados> listaAtestados = getMamAtestadosDao().obterAtestadoPorNumeroConsulta(conNumero);
		for(MamAtestados atestado : listaAtestados){
			if(!(verificarPerfilMarcacao()) && !validarProcessoE(null, null, null, espSeq)){
			DocumentosPacienteVO documentoAtestado = setarDadosDocumentoPaciente(atestado.getMamTipoAtestado().getDescricao(),atestado.getIndImpresso());
			documentoAtestado.setAtesdado(atestado);
			listaDocumentos.add(documentoAtestado);
			}
			
		}
		//TICKET RETORNO
		obterListaDocumentosTicketRetorno(conNumero, listaDocumentos, gradeSeq);

		//Receituario Cuidado																nao comitar assim
		obterListaReceituarioCuidado(conNumero, listaDocumentos, espSeq, verificarProcesso);
		// Solicitacao Procedimento
		atendimentoPacientesAgendadosAuxiliarON.obterListaDocumentosHemoterapia(conNumero, listaDocumentos);
		
		/**
		 * ON Auxiliar para correcao de erro NCSS de PMD(Excesso de linhas Fisicas)
		 * A ON abaixo chama os metodos necessÃ¡rios para obterListaDocumentosPaciente
		 * A classe atual esta cheia. Recomenda-se usar a auxiliar.
		 */
		atendimentoPacientesAgendadosAuxiliarON.obterListaDocumentosPaciente(listaDocumentos, conNumero, gradeSeq, vSituacaoCancelado, vSituacaoPendente, paramSolicitacaoExames);
								
		/**
		 * ORADB: Procedure p_popula_tela_laudo_solic
		 * @throws ApplicationBusinessException 
		 *  
		*/
//		listaDocumentos = obterListaDocumentosSolicitacaoExames(conNumero, listaDocumentos, vSituacaoCancelado, vSituacaoPendente);
		
		return listaDocumentos;
	}
	
	private void carregaEvolucaoeAnamnese(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Boolean vProntoAtendimento, Boolean verificarProcesso, Short espSeq) throws ApplicationBusinessException {
		// Anamnese
		Short paramProcessoAnamnese = retornarCodigoProcessoAnamnese();
		List<MamAnamneses> listaAnamneses = mamAnamnesesDAO.obterAnamnesesPorNumeroConsultaEIndPendente(conNumero);
		for (MamAnamneses anamnese : listaAnamneses) {
			if ((retornarParametroImprimeAnamnese().equalsIgnoreCase("S") || vProntoAtendimento)
					&& !((anamnese.getPendente() == DominioIndPendenteAmbulatorio.P || verificarPerfilMarcacao())
							&& (!verificarProcesso || !validarProcessoE(null, null, paramProcessoAnamnese, espSeq)))) {
				DocumentosPacienteVO documentoAnamnese = setarDadosDocumentoPaciente("Anamnese", anamnese.getImpresso());
				documentoAnamnese.setAnamnese(anamnese);
				listaDocumentos.add(documentoAnamnese);
			}
		}

		// Evolucao
		Short paramProcessoEvolucao = retornarCodigoProcessoEvolucao();
		List<MamEvolucoes> listaEvolucoes = mamEvolucoesDAO.obterEvolucoesPorNumeroConsultaEIndPendente(conNumero);
		for (MamEvolucoes evolucao : listaEvolucoes) {
			if ((retornarParametroImprimeEvolucao().equalsIgnoreCase("S") || vProntoAtendimento)
					&& !((evolucao.getPendente() == DominioIndPendenteAmbulatorio.P || verificarPerfilMarcacao())
							&& (!verificarProcesso || !validarProcessoE(null, null, paramProcessoEvolucao, espSeq)))) {
				DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente("Evolução", evolucao.getImpresso());
				documentoEvolucao.setEvolucao(evolucao);
				listaDocumentos.add(documentoEvolucao);
			}
		}
	}
	
	public void obterListaDocumentosPacienteAtestados(Integer conNumero,
			List<DocumentosPacienteVO> listaDocumentos)
			throws ApplicationBusinessException {
		String texto = "";
		List<MamAtestados> rAte = mamAtestadosDAO.obterAtestadoPorNumeroConsulta(conNumero);
		if (rAte != null) {
			for (MamAtestados atestado : rAte) {
				AtestadoVO atestadoVO = this.obterDadosAtestado(atestado.getSeq());
				if (atestadoVO != null) {					
					atestadoVO.setSeq(atestado.getSeq());
					if(atestado.getServidorValida() != null){
						atestadoVO.setEspecialidade(manterReceituarioON.obterEspecialidade(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo()));
					}
					atestadoVO.setNumeroVias(atestado.getNroVias());
					
					//obtem nome e assinatura do medico
					if(atestado.getServidorValida() != null){
						Object[] conselhoProfissional = prescricaoMedicaFacade.buscaConsProf(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo());					
						if (conselhoProfissional[1] != null) {
							atestadoVO.setNomeMedico(conselhoProfissional[1].toString()); 
						}
						if (conselhoProfissional[2] != null) {
							atestadoVO.setSiglaConselho(conselhoProfissional[2].toString()); 
						}
						if (conselhoProfissional[3] != null) {
							atestadoVO.setNumeroRegistroConselho(conselhoProfissional[3].toString()); 
						}
					}
					texto = this.popularTelaAtestado(conNumero, atestadoVO.getTipoAtestado());					
					if (texto != null && !texto.isEmpty()) {
						DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente(texto, atestado.getIndImpresso());
						documentoEvolucao.setAtestado(atestadoVO);
						listaDocumentos.add(documentoEvolucao);					
					}
				}				
			}			
		}
	}

	public void obterListaReceituarioCuidado(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Short espSeq, Boolean verificarProcesso) throws ApplicationBusinessException {

		Integer vMatricula = null; 
		Short vVinculo = null;
		
		Short paramReceitaTipoCuidado = retornarCodigoReceitaTipoCuidado();
		List<MamReceituarioCuidadoVO> listaReceitaCuidado = mamReceituarioCuidadoDAO.pesquisarDocumentoImpressoSituacao(conNumero);
		List<MamTipoReceitProcessos> listaTipoReceita = mamTipoReceitProcessosDAO.listarMamTipoReceitProcessosCodigo(paramReceitaTipoCuidado);
		Boolean vValidaProc = false; 
		
		if (!listaReceitaCuidado.isEmpty()) {
			List<MamReceituarioCuidadoVO> listaRegistro = mamReceituarioCuidadoDAO.pesquisarRegistro(listaReceitaCuidado.get(0).getSeq());
			if (listaRegistro != null) {
				vMatricula = listaRegistro.get(0).getSerMatriculaValida();
				vVinculo = listaRegistro.get(0).getSerVinCodigoValida();
			}
		}
		
		
		if (!listaTipoReceita.isEmpty()) {
		for (MamTipoReceitProcessos tipoReceita : listaTipoReceita) {
				if (!verificarProcesso || validarProcesso(null, null, tipoReceita.getId().getRocSeq())) {
					vValidaProc = true;
				break;
			}
		}
		}
		
		Integer qtdReceituarios = 0;
		for (MamReceituarioCuidadoVO receitaCuidado : listaReceitaCuidado) {
			List<MamReceituarioCuidadoVO> rCuidadoVO = this.obterDadosReceituarioCuidaddo(receitaCuidado.getSeq(), conNumero, espSeq);
		
			atribuirMedicoReceituario(vMatricula, vVinculo, rCuidadoVO);
		
			if (receitaCuidado.getPendente() != DominioIndPendenteAmbulatorio.V) {
				if ((receitaCuidado.getPendente() == DominioIndPendenteAmbulatorio.P || !verificarPerfilMarcacao()) && vValidaProc == false) {
					break;
				}
			}

                qtdReceituarios++;
                String texto;
				
				if (qtdReceituarios == 1){
					texto = "Cuidado";
				} else {
					texto = "Cuidado - " + qtdReceituarios; 
				}

				DocumentosPacienteVO documentoReceitaCuidado;
				documentoReceitaCuidado = setarDadosDocumentoPaciente(texto+qtdReceituarios, receitaCuidado.getImpresso()); 
				documentoReceitaCuidado.setReceituarioCuidado(rCuidadoVO);
				listaDocumentos.add(documentoReceitaCuidado);
			}
		}
	

	private void atribuirMedicoReceituario(Integer vMatricula, Short vVinculo, List<MamReceituarioCuidadoVO> receituarioCuidadoVO) throws ApplicationBusinessException {
		String assinaturaFormatado;
		if(this.obterRegistroMedico(vMatricula, vVinculo) != null){
			for(MamReceituarioCuidadoVO rCuidadoVO : receituarioCuidadoVO){
				rCuidadoVO.setNomeMedico(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNome());
				
				if (this.obterRegistroMedico(vMatricula, vVinculo).get(0).getSiglaConselho() != null){
					rCuidadoVO.setSiglaConselho(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getSiglaConselho());
				}
				if(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNumeroRegistroConselho() != null){
					rCuidadoVO.setNumeroRegistroConselho(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNumeroRegistroConselho());
				}
				
				if(rCuidadoVO.getNomeMedico() != null && rCuidadoVO.getSiglaConselho() != null && rCuidadoVO.getNumeroRegistroConselho() != null) {
					assinaturaFormatado = rCuidadoVO.getNomeMedico().concat(" - ").concat(rCuidadoVO.getSiglaConselho()).concat(" ").concat(rCuidadoVO.getNumeroRegistroConselho());
					rCuidadoVO.setAssinaturaFormatado(assinaturaFormatado);
				}else if(rCuidadoVO.getNomeMedico() != null &&  rCuidadoVO.getSiglaConselho() != null){
					assinaturaFormatado = rCuidadoVO.getNomeMedico().concat(" - ").concat(rCuidadoVO.getSiglaConselho());
					rCuidadoVO.setAssinaturaFormatado(assinaturaFormatado);
				}else if(rCuidadoVO.getNomeMedico() != null && rCuidadoVO.getNumeroRegistroConselho() != null){
					assinaturaFormatado = rCuidadoVO.getNomeMedico().concat(" ").concat(rCuidadoVO.getNumeroRegistroConselho());
					rCuidadoVO.setAssinaturaFormatado(assinaturaFormatado);
				}else if(rCuidadoVO.getNomeMedico() != null){
					rCuidadoVO.setAssinaturaFormatado(rCuidadoVO.getNomeMedico());
			}			
		}
	}
}
	
	public void realizarDocQuestionario(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, String vSituacaoPendente) {
		List<AelRespostaQuestao> aelRespostaQuestoes = examesFacade.pesquisarRespostaPorConsultaPendente(conNumero,vSituacaoPendente);
		if(aelRespostaQuestoes != null && !aelRespostaQuestoes.isEmpty()){
			Integer seqQuestionario = null;
			for(AelRespostaQuestao questao : aelRespostaQuestoes) {
				if(seqQuestionario == null || !seqQuestionario.equals(questao.getQuestionario().getSeq())){
					seqQuestionario = questao.getQuestionario().getSeq();
					questao.getQuestionario().getSeq();
					if(questao.getAelItemSolicitacaoExames().getIndInfComplImp() == null){
						questao.getAelItemSolicitacaoExames().setIndInfComplImp(false);
					}
					DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente("Questionário",questao.getAelItemSolicitacaoExames().getIndInfComplImp());
					documentoEvolucao.setRespostaQuestao(questao);
					listaDocumentos.add(documentoEvolucao);
				}
			}
		}
	}

//	private void obterListaDocumentosPacienteAtestados(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException {
//		String texto = "";
//		List<MamAtestados> rAte = mamAtestadosDAO.obterAtestadoPorNumeroConsulta(conNumero);
//		if (rAte != null) {
//			for (MamAtestados atestado : rAte) {
//				AtestadoVO atestadoVO = this.obterDadosAtestado(atestado.getSeq());
//				if (atestadoVO != null) {
//					atestadoVO.setSeq(atestado.getSeq());
//					atestadoVO.setEspecialidade(manterReceituarioON.obterEspecialidade(atestado.getSerMatriculaValida(), atestado.getSerVinCodigoValida()));
//					atestadoVO.setNumeroVias(atestado.getNroVias());
//					
//					//obtem nome e assinatura do medico
//					Object[] conselhoProfissional = prescricaoMedicaFacade.buscaConsProf(atestado.getSerMatriculaValida(), atestado.getSerVinCodigoValida());					
//					if (conselhoProfissional[1] != null) {
//						atestadoVO.setNomeMedico(conselhoProfissional[1].toString()); 
//					}
//					if (conselhoProfissional[2] != null) {
//						atestadoVO.setSiglaConselho(conselhoProfissional[2].toString()); 
//					}
//					if (conselhoProfissional[3] != null) {
//						atestadoVO.setNumeroRegistroConselho(conselhoProfissional[3].toString()); 
//					}
//					
//					texto = this.popularTelaAtestado(conNumero, atestadoVO.getTipoAtestado());					
//					if (texto != null && !texto.isEmpty()) {
//						DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente(texto, atestado.getIndImpresso().equals("S"));
//						documentoEvolucao.setAtestado(atestadoVO);
//						listaDocumentos.add(documentoEvolucao);					
//					}
//				}				
//			}			
//		}
//	}
	
//	private void obterListaReceituarioCuidado(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException {
//	
//		Integer vMatricula = null; 
//		Short vVinculo = null;
//		
//		Short paramReceitaTipoCuidado = retornarCodigoReceitaTipoCuidado();
//		List<MamReceituarioCuidadoVO> listaReceitaCuidado = mamReceituarioCuidadoDAO.pesquisarDocumentoImpressoSituacao(conNumero);
//		List<MamTipoReceitProcessos> listaTipoReceita = mamTipoReceitProcessosDAO.listarMamTipoReceitProcessosCodigo(paramReceitaTipoCuidado);
//		Boolean vValidaProc = false; 
//		
//		if (!listaReceitaCuidado.isEmpty()) {
//			List<MamReceituarioCuidadoVO> listaRegistro = mamReceituarioCuidadoDAO.pesquisarRegistro(listaReceitaCuidado.get(0).getSeq());
//			if (listaRegistro != null) {
//				vMatricula = listaRegistro.get(0).getSerMatriculaValida();
//				vVinculo = listaRegistro.get(0).getSerVinCodigoValida();
//			}
//		}
//		
//		
//		if (!listaTipoReceita.isEmpty()) {
//		for (MamTipoReceitProcessos tipoReceita : listaTipoReceita) {
//				if (validarProcesso(null, null, tipoReceita.getId().getRocSeq())) {
//					vValidaProc = true;
//				break;
//			}
//		}
//		}
//		
//		Integer qtdReceituarios = 0;
//		for (MamReceituarioCuidadoVO receitaCuidado : listaReceitaCuidado) {
//			MamReceituarioCuidadoVO rCuidadoVO = this.obterDadosReceituarioCuidaddo(receitaCuidado.getSeq());
//	
//			atribuirMedicoReceituario(vMatricula, vVinculo, rCuidadoVO);
//			
//			if (receitaCuidado.getPendente() != DominioIndPendenteAmbulatorio.V) {
//				if ((receitaCuidado.getPendente() == DominioIndPendenteAmbulatorio.P || !verificarPerfilMarcacao()) && vValidaProc == false) {
//					break;
//				}
//			}
//
//                qtdReceituarios++;
//                String texto;
//				
//				if (qtdReceituarios == 1){
//					texto = "Cuidado";
//				} else {
//					texto = "Cuidado - " + qtdReceituarios; 
//				}
//				
//				DocumentosPacienteVO documentoReceitaCuidado;
//				documentoReceitaCuidado = setarDadosDocumentoPaciente(texto+qtdReceituarios, receitaCuidado.getImpresso()); 
//				documentoReceitaCuidado.setReceituarioCuidado(rCuidadoVO);
//				listaDocumentos.add(documentoReceitaCuidado);
//			}
//		}
	

//	private void atribuirMedicoReceituario(Integer vMatricula, Short vVinculo, MamReceituarioCuidadoVO rCuidadoVO) throws ApplicationBusinessException {
//		String assinaturaFormatado;
//		if(this.obterRegistroMedico(vMatricula, vVinculo) != null){
//			
//			rCuidadoVO.setNomeMedico(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNome());
//			
//			if (this.obterRegistroMedico(vMatricula, vVinculo).get(0).getSiglaConselho() != null){
//				rCuidadoVO.setSiglaConselho(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getSiglaConselho());
//			}
//			if(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNumeroRegistroConselho() != null){
//				rCuidadoVO.setNumeroRegistroConselho(this.obterRegistroMedico(vMatricula, vVinculo).get(0).getNumeroRegistroConselho());
//			}
//				
//			if(rCuidadoVO.getNomeMedico() != null && rCuidadoVO.getSiglaConselho() != null && rCuidadoVO.getNumeroRegistroConselho() != null) {
//				assinaturaFormatado = rCuidadoVO.getNomeMedico().concat(" - ").concat(rCuidadoVO.getSiglaConselho()).concat(" ").concat(rCuidadoVO.getNumeroRegistroConselho());
//				rCuidadoVO.setAssinaturaFormatado(assinaturaFormatado);
//			}
//		}
//	}

	public List<DocumentosPacienteVO> obterListaDocumentosPacienteAnamneseEvolucao(Integer conNumero) {
		List<DocumentosPacienteVO> listaDocumentos = new ArrayList<DocumentosPacienteVO>();

		// Anamnese
		List<MamAnamneses> listaAnamneses = mamAnamnesesDAO.obterAnamnesesPeloNumeroConsultaEIndPendente(conNumero,
				new DominioIndPendenteAmbulatorio[] { DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V });
		for (MamAnamneses anamnese : listaAnamneses) {
			DocumentosPacienteVO documentoAnamnese = setarDadosDocumentoPaciente("Anamnese", anamnese.getImpresso());
			documentoAnamnese.setAnamnese(anamnese);
			listaDocumentos.add(documentoAnamnese);
		}

		// Evolução
		List<MamEvolucoes> listaEvolucoes = mamEvolucoesDAO.obterEvolucoesPeloNumeroConsultaEIndPendente(conNumero,
				new DominioIndPendenteAmbulatorio[] { 
					DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V 
					});
		for (MamEvolucoes evolucao : listaEvolucoes) {
			DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente("Evolução", evolucao.getImpresso());
			documentoEvolucao.setEvolucao(evolucao);
			listaDocumentos.add(documentoEvolucao);
		}

		return listaDocumentos;
	}

	public boolean verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(Integer conNumero) {

		// Anamnese
		List<MamAnamneses> listaAnamneses = mamAnamnesesDAO.obterAnamnesesPeloNumeroConsultaEIndPendente(conNumero,
				new DominioIndPendenteAmbulatorio[] { DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V });
		if (listaAnamneses != null && listaAnamneses.isEmpty() == false) {
			return true;
		}

		// Evolução
		List<MamEvolucoes> listaEvolucoes = mamEvolucoesDAO.obterEvolucoesPeloNumeroConsultaEIndPendente(conNumero,
				new DominioIndPendenteAmbulatorio[] { DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V });
		if (listaEvolucoes != null && listaEvolucoes.isEmpty() == false) {
			return true;
		}

		return false;
	}

	public DocumentosPacienteVO setarDadosDocumentoPaciente(String descricao, Boolean imprimiu) {
		DocumentosPacienteVO documento = new DocumentosPacienteVO();

		documento.setDescricao(descricao);
		if (imprimiu) {
			documento.setImprimiu(Boolean.TRUE);
			documento.setSelecionado(Boolean.FALSE);
		} else {
			documento.setImprimiu(Boolean.FALSE);
			documento.setSelecionado(Boolean.TRUE);
		}

		return documento;
	}
	
	/**
	 * ORADB: Function MAMC_GET_IMP_TIC_EXM
	 *  
	*/
	public Boolean existeTicketExamePaciente(Integer conNumero) throws ApplicationBusinessException {
		
		String vSituacaoPendente = null;
		AghParametros parametroSituacaoPendente = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE);
		if(parametroSituacaoPendente.getVlrTexto()!=null){
			vSituacaoPendente = parametroSituacaoPendente.getVlrTexto();	
		} 
		
		Integer vTotal = 0;
		List<AelSolicitacaoExames> listaSolicitacaoExames = examesFacade.obterSolicitacoesExamesPorConsultaESituacaoPendente(conNumero, vSituacaoPendente);
		if(listaSolicitacaoExames!=null) {
			for(AelSolicitacaoExames exame : listaSolicitacaoExames) {
				if((internacaoFacade.verificarCaracteristicaUnidadeFuncional(exame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)==false)){
					vTotal++;
				}
			}
		}
		
		if(vTotal > 0){
			return true;
		}
		
		return false;
	}
		
	/**
	 * Migração da procedures de forms VISUALIZA_CONSULTA_ANTERIOR do form mamf_atu_ambulatorio 
	 * @param consulta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public String obtemDescricaoConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException {
		consulta = this.aacConsultasDAO.obterPorChavePrimaria(consulta.getNumero());

		StringBuilder texto = new StringBuilder();
		StringBuilder textoJustificativa = new StringBuilder();
		StringBuilder textoConsultaAnterior = new StringBuilder();

		AacConsultas consultaAnterior;
		MamInterconsultas interconsulta = null;

		// Se esta consulta não foi decorrente de um atendimento na emergência térreo
		//Foi identificado um mapeamento errado em AAcConsultas, o if a seguir foi ajustado
		if (consulta.getTrgEncInternos() == null || consulta.getTrgEncInternos().isEmpty()) {
			// if (consulta.getTrgEncInterno()==null){
			consultaAnterior = atendimentoPacientesAgendadosRN.buscaConsultaAnterior(consulta);
		} else {
			consultaAnterior = aacConsultasDAO.primeiraConsultaAnterior(consulta, null, true);
		}
		if (consultaAnterior != null) {
			textoConsultaAnterior.append(atendimentoPacientesAgendadosRN.obtemDescricaoConsulta(consultaAnterior));
		}

		interconsulta = obterInterconsulta(consultaAnterior);

		if (interconsulta != null) {
			textoJustificativa.append("Justificativa pedido interconsulta: ").append(NEWLINE).append(NEWLINE).append(interconsulta.getObservacao()).append(NEWLINE).append(NEWLINE);
		}

		if (interconsulta != null && obtemRespostaConsultoria(interconsulta) != null) {
			texto.append(ASTERISCOS).append(NEWLINE).append(textoJustificativa).append(obtemRespostaConsultoria(interconsulta)).append(NEWLINE).append(ASTERISCOS).append(NEWLINE).append(NEWLINE);			
		}

		texto.append(textoConsultaAnterior).append(NEWLINE);

		return texto.toString();
	}
	
	/**
	 * @param consulta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public String obtemDescricaoConsultaAtual(AacConsultas consulta) throws ApplicationBusinessException{
		
		consulta = aacConsultasDAO.obterPorChavePrimaria(consulta.getNumero());
		
		StringBuilder texto = new StringBuilder();
		StringBuilder textoJustificativa = new StringBuilder();
		StringBuilder textoConsulta = new StringBuilder();
		MamInterconsultas interconsulta=null;
		
		if (consulta!=null){
			textoConsulta.append(atendimentoPacientesAgendadosRN.obtemDescricaoConsulta(consulta));
		}
		
		interconsulta = obterInterconsulta(consulta);

		if(interconsulta!=null){
			textoJustificativa.append("Justificativa pedido interconsulta: ");
			textoJustificativa.append(NEWLINE).append(NEWLINE).append(interconsulta.getObservacao()).append(NEWLINE).append(NEWLINE);
		}
		
		if(interconsulta !=null && obtemRespostaConsultoria(interconsulta)!=null){
			texto.append(ASTERISCOS);
			texto.append(NEWLINE);
			texto.append(textoJustificativa);
			texto.append(obtemRespostaConsultoria(interconsulta));
			texto.append(NEWLINE);
			texto.append(ASTERISCOS);
			texto.append(NEWLINE);

			texto.append(NEWLINE);

			texto.append(NEWLINE);

		}
		
		texto.append(textoConsulta)
		
		.append(getTextoPrescricaoAmbulatorialByConsulta(consulta));
		
		return texto.toString();
	}

	private String getTextoPrescricaoAmbulatorialByConsulta(AacConsultas consulta) throws ApplicationBusinessException {
		AghAtendimentos atendimento = aghuFacade.obterAtendimentoPorConsulta(consulta.getNumero());
		if (atendimento != null) {
			StringBuffer buffer = new StringBuffer();
	
			if (atendimento != null && atendimento.getSeq() != null) {
				List<MpmPrescricaoMedica> prescricoesMedicas = prescricaoMedicaFacade.pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(atendimento.getSeq(), null);
	
				for (MpmPrescricaoMedica prescricao : prescricoesMedicas) {
					List<ItemPrescricaoMedicaVO> itensPrescricaoVO = prescricaoMedicaFacade.buscarItensPrescricaoMedica(prescricao.getId(), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
							Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
					buffer.append(getTextoByItensPrescricaoMedicaVO(itensPrescricaoVO));
				}
			}
	
			return buffer.toString();
		}
		return StringUtils.EMPTY;
		
	}

	private String getTextoByItensPrescricaoMedicaVO(List<ItemPrescricaoMedicaVO> itensPrescricaoVO) {
		
		if (itensPrescricaoVO == null || itensPrescricaoVO.isEmpty()) {
			return "";
		} else {
			StringBuffer texto = new StringBuffer(144);
			texto.append("\nPRESCRIÇÃO MÉDICA - Validade de ")
					.append(DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					.append(" h. a ")
					.append(DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					.append(" h.\n")
			// AghuUtil.ordenarLista(itensPrescricaoVO, "tipo", true);
					.append("\nPRESCRIÇÃO MÉDICA - Validade de ").append(DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					.append(" h. a ").append(DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					.append(" h.\n").append("\nPRESCRIÇÃO MÉDICA - Validade de " + DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) + " h. a ")
					.append(DateUtil.dataToString(itensPrescricaoVO.get(0).getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) + " h.\n");
	
			PrescricaoMedicaTypes tipoPrescricao = null;
			Integer indice = 1;
			for(ItemPrescricaoMedicaVO item : itensPrescricaoVO){
				if(tipoPrescricao == null || !item.getTipo().equals(tipoPrescricao)){
					texto.append('\n' + item.getTipo().getTituloAmbulatorial().toUpperCase() + '\n');
					tipoPrescricao = item.getTipo();
				}
				texto.append("   " + indice + ". " + item.getDescricaoDB() + '\n');
				indice++;
			}
	
			return texto.toString().replace("<br/>", "\n");
		}
	}

	/**
	 * Verifica se o usuário pode ou não validar um determinado processo
	 * 
	 * ORADB: Package MAMK_PERFIL.MAMC_VALIDA_PROC_E
	 * 	@param pSerVinCodigo, pSerMatricula, pRocSeq, pEspSeq
	 *  
	*/
	public Boolean validarProcessoE(Short pSerVinCodigo, Integer pSerMatricula, Short pRocSeq, Short pEspSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		String login;
		Boolean vAchouNovo;
		Boolean vValida = true;
		
		if(pSerVinCodigo != null && pSerMatricula != null) {
			RapServidores servidor = registroColaboradorFacade.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(pSerMatricula, pSerVinCodigo);
			login = servidor.getUsuario();
		} else {
			login = servidorLogado.getUsuario();		
		}
		
		vValida = cascaFacade.validarPermissaoPorServidorEProcessos(login, pRocSeq, pEspSeq);
		vAchouNovo = !vValida;
		if(!vAchouNovo) {
			vValida = cascaFacade.validarPermissaoPorServidorERocSeq(login, pRocSeq);
		}
			
		return vValida;
	}
	
	/**
	 * Verifica se o usuário logado tem perfil de marcação
	 * 
	 * ORADB: C_TEM_PERFIL_MARCACAO
	 *  
	*/
	public Boolean verificarPerfilMarcacao() throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		// Busca parâmetro que indica o perfil de marcação
		String vPerfilMarcacao = null;
		AghParametros parametroSituacaoMarcacao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERFIL_MARCACAO);
		if(parametroSituacaoMarcacao.getVlrTexto()!=null){
			vPerfilMarcacao = parametroSituacaoMarcacao.getVlrTexto();	
		}
				
		return cascaFacade.usuarioTemPerfil(servidorLogado.getUsuario(), vPerfilMarcacao);
	}
	
	/**
	 * Relatório de Anamnese / Evolução
	 * @throws ApplicationBusinessException 
	 *  
	 * 
	*/
	public RelatorioAnamneseEvolucaoVO obterRelatorioAnamneseEvolucao(Integer conNumero, DominioAnamneseEvolucao tipo) throws ApplicationBusinessException {
		RelatorioAnamneseEvolucaoVO relatorio = new RelatorioAnamneseEvolucaoVO();
		
		AacConsultas consulta = aacConsultasDAO.obterConsulta(conNumero);
		
		relatorio.setProntuario(CoreUtil.formataProntuarioRelatorio(consulta.getPaciente().getProntuario()));
		relatorio.setNomePaciente(consulta.getPaciente().getNome());
		relatorio.setTitulo(tipo.getDescricao());
		relatorio.setInfoConsulta(atendimentoPacientesAgendadosRN.obterDadosBasicosConsulta(consulta).toString());
		
		if(DominioAnamneseEvolucao.A.equals(tipo)) {
			MamAnamneses anamneseCorrente = mamAnamnesesDAO.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());
			StringBuilder auxStringBuilder = atendimentoPacientesAgendadosRN.obtemDescricaoAnamnese(anamneseCorrente, false, null, false, true);
			auxStringBuilder.append(NEWLINE).append(NEWLINE).append(getAtendimentoPacientesAgendadosRN().obtemDescricaoProcedimentos(consulta));
			relatorio.setDados(auxStringBuilder.toString());
			
			StringBuilder auxStringBuilderNotas = atendimentoPacientesAgendadosRN.obtemDescricaoNotaAdicionalAna(consulta, DominioTipoNotaAdicional.N1, true);
			relatorio.setNotasAdicionais(auxStringBuilderNotas.toString());
			
		}else {
			MamEvolucoes evolucaoCorrente = mamEvolucoesDAO.obterEvolucaoAtivaPorNumeroConsulta(consulta.getNumero());
			StringBuilder auxStringBuilder = atendimentoPacientesAgendadosRN.obtemDescricaoEvolucao(evolucaoCorrente, false, null, false, true);
			auxStringBuilder.append(NEWLINE).append(NEWLINE).append(getAtendimentoPacientesAgendadosRN().obtemDescricaoProcedimentos(consulta));
			relatorio.setDados(auxStringBuilder.toString());
			
			StringBuilder auxStringBuilderNotas = atendimentoPacientesAgendadosRN.obtemDescricaoNotaAdicionalEvo(consulta, DominioTipoNotaAdicional.N1, true);
			relatorio.setNotasAdicionais(auxStringBuilderNotas.toString());	
		}
		
		return relatorio;
	}

	
	public void editarNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicional, String descricaoAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if (DominioIndPendenteAmbulatorio.V.equals(notaAdicional.getPendente())){
			notaAdicional.setPendente(DominioIndPendenteAmbulatorio.A);
			notaAdicional.setDthrMvto(new Date());
			notaAdicional.setServidorMvto(servidorLogado);
			mamNotaAdicionalEvolucoesDAO.atualizar(notaAdicional);
			mamNotaAdicionalEvolucoesDAO.flush();
			
			MamNotaAdicionalEvolucoes nae = criaNotaAdicionalEvolucoes(descricaoAdicional, consulta);
			nae.setNotaAdicionalEvolucao(notaAdicional);
			mamNotaAdicionalEvolucoesDAO.persistir(nae);	
			mamNotaAdicionalEvolucoesDAO.flush();
		}else{
			notaAdicional.setDescricao(descricaoAdicional);
			mamNotaAdicionalEvolucoesDAO.atualizar(notaAdicional);
			mamNotaAdicionalEvolucoesDAO.flush();
		}
	}
	
	private MamNotaAdicionalEvolucoes criaNotaAdicionalEvolucoes(String notaAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamNotaAdicionalEvolucoes nae = new MamNotaAdicionalEvolucoes();
		nae.setConsulta(consulta);
		nae.setDescricao(notaAdicional);
		nae.setPendente(DominioIndPendenteAmbulatorio.P);
		nae.setPaciente(consulta.getPaciente());
		nae.setDthrCriacao(new Date());
		nae.setServidor(servidorLogado);
		nae.setImpresso(false);
		return nae;
	}
	
	public MamNotaAdicionalEvolucoes inserirNotaAdicionalEvolucoes(String notaAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		MamNotaAdicionalEvolucoes nae = criaNotaAdicionalEvolucoes(notaAdicional, consulta);
		mamNotaAdicionalEvolucoesDAO.persistir(nae);
		mamNotaAdicionalEvolucoesDAO.flush();
		return nae;
	}
	
	
	public void editarNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicional, String descricaoAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if (DominioIndPendenteAmbulatorio.V.equals(notaAdicional.getPendente())){
			notaAdicional.setPendente(DominioIndPendenteAmbulatorio.A);
			notaAdicional.setDthrMvto(new Date());
			notaAdicional.setServidorMvto(servidorLogado);
			mamNotaAdicionalAnamnesesDAO.atualizar(notaAdicional);
			mamNotaAdicionalAnamnesesDAO.flush();
			
			MamNotaAdicionalAnamneses naa = criaNotaAdicionalAnamneses(descricaoAdicional, consulta);
			naa.setNotaAdicionalAnamnese(notaAdicional);
			mamNotaAdicionalAnamnesesDAO.persistir(naa);	
			mamNotaAdicionalAnamnesesDAO.flush();
		}else{
			notaAdicional.setDescricao(descricaoAdicional);
			mamNotaAdicionalAnamnesesDAO.atualizar(notaAdicional);
			mamNotaAdicionalAnamnesesDAO.flush();
		}
	}
	
	
	public MamNotaAdicionalAnamneses criaNotaAdicionalAnamneses(String notaAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamNotaAdicionalAnamneses naa = new MamNotaAdicionalAnamneses();
		naa.setConsulta(consulta);
		naa.setDescricao(notaAdicional);
		naa.setPendente(DominioIndPendenteAmbulatorio.P);
		naa.setPaciente(consulta.getPaciente());
		naa.setDthrCriacao(new Date());
		naa.setServidor(servidorLogado);		
		return naa;
	}
	
	
	public MamNotaAdicionalAnamneses inserirNotaAdicionalAnamneses(String notaAdicional, AacConsultas consulta) throws ApplicationBusinessException{
		MamNotaAdicionalAnamneses naa = criaNotaAdicionalAnamneses(notaAdicional, consulta);
		mamNotaAdicionalAnamnesesDAO.persistir(naa);
		mamNotaAdicionalAnamnesesDAO.flush();
		return naa;
	}	

	
	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.</br>
	 * 
	 * <b>Para manter a compatibilidade com o que já havia sido implementado, 
	 * foi mantido o método sem o parâmetro de tipo, este assume por default o CapitalizeEnum.PRIMEIRA</b>
	 * 
	 * ORADB: Function MPMC_MINUSCULO
	 * 
	 * @param descCid
	 */
	public String obterDescricaoCidCapitalizada(String descCid) {
		return obterDescricaoCidCapitalizada(descCid, CapitalizeEnum.PRIMEIRA);
	}
	
	
	/**
	 * Retorna a descricao (string) do CID na forma capitalizada.
	 * 
	 * ORADB: Function MPMC_MINUSCULO
	 * 
	 * @param descCid
	 * @param tipo
	 * @return
	 */
	public String obterDescricaoCidCapitalizada(String descCid, CapitalizeEnum tipo) {
		
		if (StringUtils.isBlank(descCid)) {
			return descCid; 
		}
		
		if(CapitalizeEnum.PRIMEIRA.equals(tipo)){
			return CoreUtil.capitalizaTextoFormatoAghu(descCid);
		}
		
		String[] preposicoes =  {"a", "o", "e", "as", "os", "é" ,"da", "do", "de", "das", "dos" , "se"};
		String[] rns =  {"rn","rnii","rniii","rniv","rnv","rnvi","rnvii"};
		
		Map<String, String> preposicoesExcecoes = new HashMap<String, String>();
		preposicoesExcecoes.put("de", "luca");
		
		String[] palavras = descCid.toLowerCase().split("\\ ");
		
		StringBuffer novaDescCid = new StringBuffer();
		for (int i = 0; i < palavras.length; i++) {
			String palavra = palavras[i];
			
			if(ArrayUtils.contains(preposicoes, palavra)){ // preposições
				
				if(preposicoesExcecoes != null &&  // preposições em maiúsculo em casos específicos "De Luca" 
						preposicoesExcecoes.containsKey(palavra) && 
						i+1 < palavras.length && 
						palavras[i+1].equals(preposicoesExcecoes.get(palavra))){
					novaDescCid.append(CoreUtil.capitalizaTextoFormatoAghu(palavra));
				} else {
					novaDescCid.append(palavra.toLowerCase()); // preposições em minúsculo
				}
				
			} else if(ArrayUtils.contains(rns, palavra)){
				novaDescCid.append(palavra.toUpperCase()); //rns em maiúsculo
			} else {
				novaDescCid.append(CoreUtil.capitalizaTextoFormatoAghu(palavra)); // normal
			}
			novaDescCid.append(' '); // acrescenta um espaço.
		}
		
		return novaDescCid.toString().trim();
	}
	
	/**
	 * Retorna true caso seja necessário informar o CID,
	 * false caso contrário.
	 * 
	 * ORADB: Procedure MAMP_VER_PROCED_CID
	 */
	public void verificarCidProcedimento(Integer consultaNumero, Integer prdSeq, Integer cidSeq) throws ApplicationBusinessException {
		Long numCidsParaInformar = 0l;
		Short vlrPagadorSus = 0;
		
		AacConsultas consulta = aacConsultasDAO.obterConsulta(consultaNumero);
		
		AghParametros paramPagadorSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS);
		
		if (paramPagadorSus != null && paramPagadorSus.getVlrNumerico() != null) {
			vlrPagadorSus = paramPagadorSus.getVlrNumerico().shortValue();
		}
		
		if (consulta.getPagador() != null && consulta.getPagador().getSeq().equals(vlrPagadorSus) && prdSeq != null) {
			numCidsParaInformar = vMamProcXCidDAO.pesquisarCidsParaProcedimentoAtendimentoMedicoCount(null, prdSeq);
		}		

		MamProcedimento procedimento = mamProcedimentoDAO.obterPorChavePrimaria(prdSeq);
		if (numCidsParaInformar == 0) {
			if (cidSeq != null) {
				throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.MSG_NAO_INFORME_CID_PARA_O_PROCEDIMENTO, procedimento.getDescricao());
			}
		} else {
			if (cidSeq == null) {
				throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.MSG_INFORME_CID_PARA_O_PROCEDIMENTO, procedimento.getDescricao());
			}
			
			// Não será necessario verificar se o cid é compativel (conforme era feito no AGH),
			// pois ao salvar um procedimento serão exibidos para seleção somente os
			// cids compativeis com o procedimento sendo editado
		}
	}

	public Integer getTinSeqAnamnese() throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		try {
			List<CseCategoriaProfissional> lista = cascaFacade.pesquisarCategoriaProfissional(servidorLogado);
			for(CseCategoriaProfissional categoria: lista){
			if (categoria==null) {
				return null;
			}
			AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ANAMNESE_ITEMTIPO_MED).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_ENF);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ANAMNESE_ITEMTIPO_ENF).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_NUT);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ANAMNESE_ITEMTIPO_NUTRI).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_OUTROS);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ANAMNESE_ITEMTIPO_OUTROS).getVlrNumerico().intValue();
			}
			}
		}catch(Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.MSG_NO_PARAMETRO);
		}		
		return null;
	}
	
	public Integer getTinSeqEvolucao() throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		try {
			List<CseCategoriaProfissional> lista = cascaFacade.pesquisarCategoriaProfissional(servidorLogado);
			for(CseCategoriaProfissional categoria: lista){
			if (categoria==null) {
				return null;
			}			
			AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_MED).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_ENF);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_ENF).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_NUT);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_NUTRI).getVlrNumerico().intValue();
			}
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CATEG_PROF_OUTROS);
			if (categoria.getSeq().equals(parametro.getVlrNumerico().intValue())){
				return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EVOLUCAO_ITEMTIPO_OUTROS).getVlrNumerico().intValue();
			}

			}
		}catch(Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.MSG_NO_PARAMETRO);
		}		
		return null;
	}

	
	public void salvarAnamnese(String texto, AacConsultas consulta, Integer timSeq) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamEvolucoes evolucao = mamEvolucoesDAO.obterEvolucaoAtivaPorNumeroConsulta(consulta.getNumero());
		MamAnamneses anamnese = mamAnamnesesDAO.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());
		if (evolucao!=null){
			AtendimentoPacientesAgendadosONExceptionCode.MENSAGEM_JA_EXISTE_EVOLUCAO.throwException();
		}
		if (anamnese != null && DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente()) && !anamnese.getServidorValida().equals(servidorLogado)) {
			throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.ERRO_USUARIO_DE_VALIDACAO);
		}		
		if (anamnese!=null && DominioIndPendenteAmbulatorio.P.equals(anamnese.getPendente())){
			List<MamItemAnamneses> itensAnamnese = mamItemAnamnesesDAO.pesquisarItemAnamnesesPorAnamneses(anamnese.getSeq());
			MamItemAnamneses item = itensAnamnese.isEmpty()?null:itensAnamnese.get(0);
			if (item==null){
				item=new MamItemAnamneses();				
			}
		item.setDescricao(texto);	
		if (item.getId()!=null){
			marcacaoConsultaRN.atualizarItemAnamnese(item);
		}else{
			item.setId(new MamItemAnamnesesId(anamnese.getSeq(), timSeq));
			item.setAnamnese(anamnese);
			marcacaoConsultaRN.inserirItemAnamnese(item);
			anamnese.getItensAnamneses().add(item);
		}
		}else{		
			MamAnamneses novaAnamnese=new MamAnamneses();
			novaAnamnese.setConsulta(consulta);
			novaAnamnese.setDthrCriacao(new Date());
			novaAnamnese.setPaciente(consulta.getPaciente());
			novaAnamnese.setImpresso(false);
			novaAnamnese.setPendente(DominioIndPendenteAmbulatorio.P);
			novaAnamnese.setItensAnamneses(new HashSet<MamItemAnamneses>());
			novaAnamnese.setIndAssinaAutSist(false);
			novaAnamnese.setIndImpressoPasta(false);
			if (anamnese!=null){
				anamnese.setDthrMvto(new Date());
				anamnese.setServidorMvto(servidorLogado);
				anamnese.setPendente(DominioIndPendenteAmbulatorio.A);
				anamnese = marcacaoConsultaRN.atualizarAnamnese(anamnese);
				novaAnamnese.setAnamnese(anamnese);
			}
			novaAnamnese=marcacaoConsultaRN.inserirAnamnese(novaAnamnese);		
	
			MamItemAnamneses item=new MamItemAnamneses();		
			item.setDescricao(texto);	
			item.setId(new MamItemAnamnesesId(novaAnamnese.getSeq(), timSeq));
			item.setAnamnese(novaAnamnese);
			marcacaoConsultaRN.inserirItemAnamnese(item);
			novaAnamnese.getItensAnamneses().add(item);
		}	
	}
	
	public void salvarEvolucao(String texto, AacConsultas consulta, Integer timSeq) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamEvolucoes evolucao = mamEvolucoesDAO.obterEvolucaoAtivaPorNumeroConsulta(consulta.getNumero());
		MamAnamneses anamnese = mamAnamnesesDAO.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());
		if (anamnese!=null){
			AtendimentoPacientesAgendadosONExceptionCode.MENSAGEM_JA_EXISTE_ANAMNESE.throwException();
		}
		if (evolucao != null && DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente()) && !evolucao.getServidorValida().equals(servidorLogado)) {
			throw new ApplicationBusinessException(AtendimentoPacientesAgendadosONExceptionCode.ERRO_USUARIO_DE_VALIDACAO);
		}		
		if (evolucao!=null && DominioIndPendenteAmbulatorio.P.equals(evolucao.getPendente())){
			List<MamItemEvolucoes> itensEvolucao = mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(evolucao.getSeq());
			MamItemEvolucoes item = itensEvolucao.isEmpty()?null:itensEvolucao.get(0);
			if (item==null){
				item=new MamItemEvolucoes();				
		}
		item.setDescricao(texto);
		if (item.getId()!=null){
				marcacaoConsultaRN.atualizarItemEvolucao(item);			
		}else{
			item.setId(new MamItemEvolucoesId(evolucao.getSeq(), timSeq));
			item.setEvolucao(evolucao);
				marcacaoConsultaRN.inserirItemEvolucao(item);
			evolucao.getItensEvolucoes().add(item);
		}
		}else{
			MamEvolucoes novaEvolucao=new MamEvolucoes();
			novaEvolucao.setConsulta(consulta);
			novaEvolucao.setDthrCriacao(new Date());
			novaEvolucao.setPaciente(consulta.getPaciente());
			novaEvolucao.setImpresso(false);
			novaEvolucao.setPendente(DominioIndPendenteAmbulatorio.P);
			novaEvolucao.setItensEvolucoes(new HashSet<MamItemEvolucoes>());
			novaEvolucao.setIndImpressoPasta(false);
			if (evolucao!=null){
				evolucao.setDthrMvto(new Date());
				evolucao.setServidorMvto(servidorLogado);
				evolucao.setPendente(DominioIndPendenteAmbulatorio.A);
				evolucao = marcacaoConsultaRN.atualizarEvolucao(evolucao);
				novaEvolucao.setEvolucao(evolucao);
			}
			novaEvolucao=marcacaoConsultaRN.inserirEvolucao(novaEvolucao);
			
			MamItemEvolucoes item=new MamItemEvolucoes();
			item.setDescricao(texto);
			item.setId(new MamItemEvolucoesId(novaEvolucao.getSeq(), timSeq));
			item.setEvolucao(novaEvolucao);
			marcacaoConsultaRN.inserirItemEvolucao(item);
			novaEvolucao.getItensEvolucoes().add(item);
		}
	}
	
	
	private MamSituacaoAtendimentos obtemSituacaoAtendimento() throws ApplicationBusinessException{
		AghParametros item = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_EM_ATEND); 
		MamSituacaoAtendimentos situacaoAtendimento = mamSituacaoAtendimentosDAO.obterPorChavePrimaria(item.getVlrNumerico().shortValue()); 
		return situacaoAtendimento;
	}
	
	private MamSituacaoAtendimentos obtemSituacaoReaberto() throws ApplicationBusinessException{
		AghParametros item = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_REABERTO); 
		MamSituacaoAtendimentos situacaoAtendimento = mamSituacaoAtendimentosDAO.obterPorChavePrimaria(item.getVlrNumerico().shortValue()); 
		return situacaoAtendimento;
	}	
	
	
	/**
	 * P_ATLZ_CONTROL_AGUARD LIVRE
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void atualizaControleAguardandoLivre(Integer numeroConsulta, Date dthrMovimento, String nomeMicrocomputador) throws ApplicationBusinessException{		
		MamControles controle = ambulatorioFacade.obterMamControlePorNumeroConsulta(numeroConsulta);
		if (controle!=null){ 
			if (controle.getSituacao().equals(DominioSituacaoControle.L)){// VAI PASSAR PARA EM USO atualiza log_em_usos    
				
				MamSituacaoAtendimentos situacao=null;				
				if (controle.getSituacaoAtendimento().getAtendConcluido()){
					situacao=this.obtemSituacaoReaberto();
				}else{
					situacao=this.obtemSituacaoAtendimento();
				}	
				controle.setSituacao(DominioSituacaoControle.U);
				controle.setDthrMovimento(dthrMovimento);
				controle.setSituacaoAtendimento(situacao);
				try {
					marcacaoConsultaRN.atualizarControles(controle, nomeMicrocomputador);
				} catch (ApplicationBusinessException e) {
					AtendimentoPacientesAgendadosONExceptionCode.MAM_00193.throwException();
				}
				
				Short extratoControleSeqP  = marcacaoConsultaRN.obterExtratoControleSeqP(controle);
				MamExtratoControles extratoControle = new MamExtratoControles();
				MamExtratoControlesId extratoControleId = new MamExtratoControlesId();
				extratoControleId.setSeqp(extratoControleSeqP);
				extratoControleId.setCtlSeq(controle.getSeq());
				extratoControle.setId(extratoControleId);
				extratoControle.setDthrMovimento(dthrMovimento);
				extratoControle.setSituacaoAtendimento(situacao);
				try {
					marcacaoConsultaRN.inserirExtratoControles(extratoControle, nomeMicrocomputador);
					controle.getExtratoControles().add(extratoControle);
				} catch (ApplicationBusinessException e) {
					AtendimentoPacientesAgendadosONExceptionCode.MAM_00946.throwException();
				}
			}
		}
		this.insereLogEmUso(numeroConsulta);
	}
	
	/**
	 * P_ATLZ_CONTROL_AGUARD USO
	 * @throws ApplicationBusinessException 
	 */
	public void atualizaControleAguardandoUso(Integer consultaNumero, Date dthrMovimento) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamControles controle = ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaNumero);
		Boolean atualizaUso = false;
		
		if (controle!=null){
			if (controle.getSituacao().equals(DominioSituacaoControle.U)){
				// aqui fazer o teste de ser o mesmo usuário ou usuário diferente para ver se atualiza ou não o log em usos
				if (!servidorLogado.equals(controle.getServidorAtualiza())){
					atualizaUso = true;
				}
			}
		}
		if (atualizaUso){
			this.insereLogEmUso(consultaNumero);
		}
	}
	
	public void insereLogEmUso(Integer numeroConsulta) throws ApplicationBusinessException{
		MamLogEmUsos logEmUso = new MamLogEmUsos();
		AacConsultas consulta = ambulatorioFacade.obterConsultaPorNumero(numeroConsulta);
		logEmUso.setConsulta(consulta);
		logEmUso.setPaciente(consulta.getPaciente());
		try{
			atendimentoPacientesAgendadosRN.inserirLogEmUso(logEmUso);
		}catch (BaseException e) {
			AtendimentoPacientesAgendadosONExceptionCode.MAM_00195.throwException();
		}		
	}
	
	public void atualizarIndImpressaoAnamnese(Long seqAnamnese) {
		MamAnamneses anamnese = mamAnamnesesDAO.obterPorChavePrimaria(seqAnamnese);
		atendimentoPacientesAgendadosRN.atualizarIndImpressaoAnamnese(anamnese);
		
	}

	public void atualizarIndImpressaoEvolucao(Long seqEvolucao) {
		MamEvolucoes evolucao = mamEvolucoesDAO.obterPorChavePrimaria(seqEvolucao);
		atendimentoPacientesAgendadosRN.atualizarIndImpressaoEvolucao(evolucao);
	}
	
	public void atualizarIndImpressaoAtestado(Long seqAtestado) {
		MamAtestados atestado = getMamAtestadosDao().obterPorChavePrimaria(seqAtestado);
		getAtendimentoPacientesAgendadosRN().atualizarIndImpressaoAtestado(atestado);
	}
	
	public void atualizarIndImpressaoReceituario(Long seqReceita) throws ApplicationBusinessException {
		MamReceituarios receita = mamReceituariosDAO.obterPorChavePrimaria(seqReceita);
		atendimentoPacientesAgendadosRN.atualizarIndImpressaoReceituario(receita);
	}
	
	/**
	 * Obtem a lista de documentos para geração de certificacao digital
	 * @param conNumero
	 *  
	*/
	public List<DocumentosPacienteVO> obterListaDocumentosPacienteParaCertificacao(Integer conNumero) throws ApplicationBusinessException {
		List<DocumentosPacienteVO> listaDocumentos = new ArrayList<DocumentosPacienteVO>();
		
		// Parametro que indica qual é a situacao para exame pendente
		String vSituacaoPendente = retornarCodigoExameSituacaoPendente();
		
		// Ticket Exame
		List<Integer> listaSeqSolicitacaoExames = examesFacade.obterSeqSolicitacoesExamesPorConsultaCertificacaoDigital(conNumero, vSituacaoPendente);
		for(Integer seqSolicitacaoExames : listaSeqSolicitacaoExames) {
			AelSolicitacaoExames exame = examesFacade.obterAelSolicitacaoExamePorChavePrimaria(seqSolicitacaoExames);
			DocumentosPacienteVO documentoSolicitacaoExame =  new DocumentosPacienteVO();
			documentoSolicitacaoExame.setSolicitacaoExame(exame);
			listaDocumentos.add(documentoSolicitacaoExame);
		}
		
		
		// Anamnese
		List<MamAnamneses> listaAnamneses = getMamAnamnesesDAO().obterAnamnesesPorNumeroConsultaEIndPendente(conNumero);
		List<MamNotaAdicionalAnamneses> listaNaa = mamNotaAdicionalAnamnesesDAO.obterNotaAdicionalAnamnesesConsultaCertificacaoDigital(conNumero);
		for(MamAnamneses anamnese : listaAnamneses) {
			if( (listaNaa!=null && !listaNaa.isEmpty()) || (anamnese.getPendente() == DominioIndPendenteAmbulatorio.P) ) {
				DocumentosPacienteVO documentoAnamnese =  new DocumentosPacienteVO();
				documentoAnamnese.setAnamnese(anamnese);
				listaDocumentos.add(documentoAnamnese);
			}
		}
		
		// Evolução
		List<MamEvolucoes> listaEvolucoes = mamEvolucoesDAO.obterEvolucoesPorNumeroConsultaEIndPendente(conNumero);
		List<MamNotaAdicionalEvolucoes> listaNae = mamNotaAdicionalEvolucoesDAO.obterNotaAdicionalEvolucoesConsultaCertificacaoDigital(conNumero); 
		
		for(MamEvolucoes evolucao : listaEvolucoes) {
			if( (listaNae != null && !listaNae.isEmpty()) || (evolucao.getPendente() == DominioIndPendenteAmbulatorio.P) ) {
				DocumentosPacienteVO documentoEvolucao =  new DocumentosPacienteVO();
				documentoEvolucao.setEvolucao(evolucao);
				listaDocumentos.add(documentoEvolucao);
			}
		}
		
		return listaDocumentos;
	}

	
	public Short retornarCodigoReceitaTipoCuidado() throws ApplicationBusinessException{
		
		Short codigoRecCuidado = 0;
		AghParametros parametroCodReceitaTipoCuidado = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TP_REC_CUIDADOS);
		if(parametroCodReceitaTipoCuidado.getVlrNumerico()!=null){
			codigoRecCuidado = parametroCodReceitaTipoCuidado.getVlrNumerico().shortValue();	
		}
		return codigoRecCuidado;
	}
	
	/**
	 * @ORADB: Function CF_MEDICO_REGISTRO
	 *  
	*/
	public List<ConselhoProfissionalServidorVO> obterRegistroMedico(Integer vMatricula, Short vVinculo) throws ApplicationBusinessException {

		List<ConselhoProfissionalServidorVO> listaRegistroMedico = new ArrayList<ConselhoProfissionalServidorVO>();
		Object[] buscaConsProf = prescricaoMedicaFacade.buscaConsProf(vMatricula, vVinculo);
		if (buscaConsProf != null) {
			this.conselhoProfissionalServidorVO = new ConselhoProfissionalServidorVO();
			if (buscaConsProf[1] != null) {
				conselhoProfissionalServidorVO.setNome(buscaConsProf[1].toString());
			}
			if (buscaConsProf[2] != null) {
				conselhoProfissionalServidorVO.setSiglaConselho(buscaConsProf[2].toString());
			}
			if (buscaConsProf[3] != null) {
				conselhoProfissionalServidorVO.setNumeroRegistroConselho(buscaConsProf[3].toString());
			}
			if (buscaConsProf[4] != null) {
				conselhoProfissionalServidorVO.setCpf(Long.parseLong(buscaConsProf[4].toString()));
			}

			listaRegistroMedico.add(conselhoProfissionalServidorVO);
		}
		return listaRegistroMedico;
	}
	
	/**
	 * Atualiza MamSolicitacaoRetorno indicando que foi impresso.
	 * @param seq
	 */
	public void atualizarIndImpressaoSolicitacaoRetorno(Long seq) {		
		MamSolicitacaoRetorno solicitacaoRetorno = mamSolicitacaoRetornoDAO.obterPorChavePrimaria(seq);
		solicitacaoRetorno.setIndImpresso("S");
		mamSolicitacaoRetornoDAO.atualizar(solicitacaoRetorno);		
	}
	
	public Short retornarCodigoParamPS3() throws ApplicationBusinessException {
		Short codigoParam = 0;
		AghParametros aghParam = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_SOL_RETORNO);
		if(aghParam.getVlrNumerico()!=null){
			codigoParam = aghParam.getVlrNumerico().shortValue();	
		}
		return codigoParam;
	}
	/**
	 * #43033 ON2
	 * VALIDAÇÃO RETORNO C1
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 */
	public Boolean verificarSolicitacaoRetornoDocumentoImpresso(
			AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso)
			throws NumberFormatException, ApplicationBusinessException {
	
		if(consulta != null && consulta.getNumero() != null){
			List<MamSolicitacaoRetornoDocumentoImpressoVO> listaObjetoVO = mamSolicitacaoRetornoDAO.obterDocumentoASerImpresso(consulta.getNumero(), false);
			
			AghParametros parametro = new AghParametros();
			parametro.setNome(AghuParametrosEnum.P_SEQ_PROC_ALTA.toString());
			
			for (MamSolicitacaoRetornoDocumentoImpressoVO objetoVO : listaObjetoVO) {
				
				if(objetoVO.getIndPendente().equals("V") || (objetoVO.getIndPendente().equals("P") && (!verificarProcesso || validarProcesso(null, null, Short.valueOf(
						parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro).get(0).getVlrNumerico().toString()))))){
					vTemImp = Boolean.TRUE;
				}
			}
		}
		return vTemImp;
	}

	/**
	 * #43033 ON4 - CHAMADA PROCEDURE P3
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 */
	private void obterListaDocumentosTicketRetorno(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Integer gradeSeq) throws NumberFormatException, ApplicationBusinessException{
		List<CursorCurTicketVO> listaCTicket = mamSolicitacaoRetornoDAO.obterCursorCurTicketVO(conNumero, true);
		Integer vQtdeTicket = 0;
		for (CursorCurTicketVO cursorCurTicketVO : listaCTicket) {
			vQtdeTicket = vQtdeTicket + 1;
			String retorno = pPopulaTelaTicket(cursorCurTicketVO, vQtdeTicket, gradeSeq);
			if(retorno != null){
				DocumentosPacienteVO documentoTicketRetorno = setarDadosDocumentoPaciente(retorno , cursorCurTicketVO.getIndImpresso().equals("S") ? Boolean.TRUE : Boolean.FALSE);
				documentoTicketRetorno.setDocumentoTicketRetorno(cursorCurTicketVO);
				listaDocumentos.add(documentoTicketRetorno);
			}
		}
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 * @ORADB: P_POPULA_TELA_TICKET
	 * #43033 PS3 Substitui Procedure PS3
	 * Este procedimento tem o objetivo de incluir um novo registro, 
	 * relacionado ao documento, na lista de documentos a serem impressos 
	 * (Modal Impressões do Ambulatório da estória #43025). 
	 */
	public String pPopulaTelaTicket(CursorCurTicketVO cursorCurTicketVO, Integer vQtdeTicket, Integer gradeSeq) throws NumberFormatException, ApplicationBusinessException{
		
		String vTexto;
		Short pSeqProcTicket = retornarCodigoParamPS3();
		
		AacGradeAgendamenConsultasVO vo = this.aacGradeAgendamentoConsutlaDAO.obterAacGradeAgendamenConsultasVOPorSeq(gradeSeq);
		
		if(!cursorCurTicketVO.getIndPendente().matches("V")){
			if((cursorCurTicketVO.getIndPendente().matches("P") || verificarPerfilMarcacao()) 
					&& !validarProcessoE(null, null, pSeqProcTicket, vo.getEspSeq())){
				return null;
			}
		}
			
		if(vQtdeTicket == 1){
			vTexto = "Sol. Retorno";
		}else{
			vTexto = "Sol. Retorno - ".concat(vQtdeTicket.toString());
		}
		return vTexto;
	}
	
	/**
	 * #43033 P1
	 * @ORADB: MAMC_TICKET_RECEITA
	 * @param pSorSeq
	 * @param pOrigem
	 * @return
	 */
	public String mamcTicketReceita(Integer pSorSeq, String pOrigem){
		
		Integer vConNum;
		Short vQtdeMeses;
		String vIndQtdeMeses;
		String vIndAposExames;
		String vIndData;
		Date vDataRetorno;
		String vIndSeNecessario;
		String vIndDiaSemana;
		Short vDiaSemana;
		String vTxtDiaSemana;
		Short vSorEspSeq;
		Integer vSorEqpSeq;
		Integer vConPacCodigo3;
		Integer vPacProntuario3;
		String vpacNome3;
		
		//TEXTO 1
		Integer vConNumero;
		String vConDtConsulta;
		String vConDtConsulta1;
		String vConTpsTipo1;
		String vVuslSigla;
		Byte vVuslSala;

		//TEXTO 2
		String vCaaDescricao;
		String vEspNomeReduzido;
		
		//TEXTO 3
		String vEqpNome;
		String vRapNome1;
		
		//TEXTO 4
		Integer vConPacCodigo;
		Integer vPacProntuario;
		String vPacNome2;
		
		//TEXTO 5
		Integer vGrdGrade;
		Short vConVinc;
		Integer vConSerMatriculaConsultado;
		String vCctCCusto;
		
		//TEXTO 6
		String vConTpsTipo2;
		String vUser;
		String vPreNome;
		
		//CHAR(10) FUNCTION
		char char10 = (char)10;
		StringBuilder vTexto = new StringBuilder(500);
		vTexto.append("");
		
		List<CursorCTicketRetornoVO> listaCTicketRetornoVOs = this.mamSolicitacaoRetornoDAO.obterCursorTicketRetorno(Long.valueOf(pSorSeq));
		vConNum = listaCTicketRetornoVOs.get(0).getConNumeroRetorno();
		vIndQtdeMeses = listaCTicketRetornoVOs.get(0).getIndQtdeMeses();
		vQtdeMeses = listaCTicketRetornoVOs.get(0).getQtdeMeses();
		vIndAposExames = listaCTicketRetornoVOs.get(0).getIndAposExames();
		vIndData = listaCTicketRetornoVOs.get(0).getIndData();
		vDataRetorno = listaCTicketRetornoVOs.get(0).getDataRetorno();
		vIndSeNecessario = listaCTicketRetornoVOs.get(0).getIndSeNecessario();
		vIndDiaSemana = listaCTicketRetornoVOs.get(0).getIndDiaSemana();
		vDiaSemana = listaCTicketRetornoVOs.get(0).getDiaSemana();
		vSorEqpSeq = listaCTicketRetornoVOs.get(0).getEqpSeq();
		vSorEspSeq = listaCTicketRetornoVOs.get(0).getEspSeq();
		vConPacCodigo3 = listaCTicketRetornoVOs.get(0).getCodigo();
		vPacProntuario3 = listaCTicketRetornoVOs.get(0).getProntuario();
		vpacNome3 = listaCTicketRetornoVOs.get(0).getNome();

		if(vConNum == null){
			List<CursorConDadosVO> listCursorConDadosVOs = this.aacGradeAgendamentoConsutlaDAO.obterCursorCConDadosVO(Long.valueOf(pSorSeq));
			
			for (CursorConDadosVO rCon : listCursorConDadosVOs) {
				vTexto.append("Ao sair do consultório passe na recepção da zona ").append(validarObjetoNulo(rCon.getSigla())).append(". Caso contrário, sua consulta perderá a validade.").append(char10).append("Marcar reconsulta na especialidade ").append(validarObjetoNulo(rCon.getNomeReduzido())).append(" para a equipe ").append(validarObjetoNulo(rCon.getNome())).append(ESPACO);
				
				if(vSorEqpSeq == null && vSorEspSeq == null){
					if(rCon.getPreSerVinCodigo() != null && rCon.getPreSerMatricula() != null){
						List<CursorCurPreVO> listaCurPreVOs = this.registroColaboradorFacade.obterCursorCurPreVO(rCon.getPreSerMatricula(), rCon.getPreSerVinCodigo());
						vPreNome = listaCurPreVOs.get(0).getNome();
						vTexto.append("para o profissional ").append(validarObjetoNulo(vPreNome)).append(ESPACO);
					}
				}
				
				if(vIndAposExames.equals("S")){
					vTexto.append("após exames.");
				}else if(vIndQtdeMeses.equals("S")){
					if(Integer.valueOf(vQtdeMeses) > 1){
						vTexto.append("após ").append(vQtdeMeses).append(" meses.");
					}else if(Integer.valueOf(vQtdeMeses) == 1){
						vTexto.append("após ").append(vQtdeMeses).append("' mês.");
					}
				}else if(vIndData.equals("S")){
					vTexto.append("no dia ").append(validarObjetoNulo(DateUtil.obterDataFormatada(vDataRetorno, DateConstants.DATE_PATTERN_DDMMYYYY))).append(PONTO);
				}else if(vIndSeNecessario.equals("S")){
					vTexto.append("se necessário.");
				}else if(vIndDiaSemana.equals("S")){
					vTxtDiaSemana = "";
					switch (vDiaSemana) {
					case 2:
						vTxtDiaSemana = "segunda-feira";
						break;
					case 3:
						vTxtDiaSemana = "terça-feira";
						break;
					case 4:
						vTxtDiaSemana = "quarta-feira";
						break;
					case 5:
						vTxtDiaSemana = "quinta-feira";
						break;
					case 6:
						vTxtDiaSemana = "sexta-feira";
						break;
					default:
						break;
					}
					
					vTexto.append("na ").append(validarObjetoNulo(vTxtDiaSemana)).append(PONTO);
				}
				vTexto.append(char10).append(char10).append("Paciente:").append(validarObjetoNulo(vConPacCodigo3)).append(ESPACO_5).append(" Prontuário: ").append(validarObjetoNulo(vPacProntuario3)).append("  -  ").append(validarObjetoNulo(vpacNome3));
			}
		}else{
			List<CursorCTicketDadosVO> listaCTicketDadosVO = this.aacGradeAgendamentoConsutlaDAO.obterCursorCTicketDadosVO(vConNum);

			listaCTicketDadosVO.get(0).formatarCampos();
			
			vConNumero = listaCTicketDadosVO.get(0).getNumero();
			vConDtConsulta = listaCTicketDadosVO.get(0).getDtConsultaFormatado();
			vConDtConsulta1 = listaCTicketDadosVO.get(0).getHoraConsultaFormatado();
			vConVinc = listaCTicketDadosVO.get(0).getSerVinCodigoConsultado();
			vConSerMatriculaConsultado = listaCTicketDadosVO.get(0).getSerMatriculaConsultado();
			vCctCCusto = listaCTicketDadosVO.get(0).getDescricaoCentroCustos();
			vConTpsTipo1 = listaCTicketDadosVO.get(0).getDiaSemanaFormatado();
			vCaaDescricao = listaCTicketDadosVO.get(0).getDescricaoCondicaoAtendimento();
			vConTpsTipo2 = listaCTicketDadosVO.get(0).getIndExcedeProgramacaoFormatado();
			vEspNomeReduzido = listaCTicketDadosVO.get(0).getNomeReduzido();
			vVuslSigla = listaCTicketDadosVO.get(0).getSigla();
			vVuslSala = Byte.parseByte(listaCTicketDadosVO.get(0).getSalaFormatado());
			vEqpNome = listaCTicketDadosVO.get(0).getNomeEquipe();
			
			listaCTicketDadosVO.get(0).formatarRetornoProcedure(aacBuscaNomeProf(listaCTicketDadosVO.get(0).getGradePreSerMatricula(), listaCTicketDadosVO.get(0).getGradePreSerVinCodigo()));
			vRapNome1 = listaCTicketDadosVO.get(0).getRetornoProcedureP2();
			
			vConPacCodigo = listaCTicketDadosVO.get(0).getPacCodigo();
			vPacProntuario = listaCTicketDadosVO.get(0).getProntuario();
			vPacNome2 = listaCTicketDadosVO.get(0).getNomePac();
			vGrdGrade = listaCTicketDadosVO.get(0).getSeq();
			vUser = listaCTicketDadosVO.get(0).getUser();
		
			if(pOrigem.equals("RECEITA")){
				
				vTexto.append("Nro. Cons: ").append(validarObjetoNulo(vConNumero)).append(ESPACO_5).append(" Data: ").append(validarObjetoNulo(vConDtConsulta)).append(ESPACO_5).append(" Hora: ").append(validarObjetoNulo(vConDtConsulta1)).append(ESPACO_5).append(" Dia: ").append(validarObjetoNulo(vConTpsTipo1)).append(ESPACO_5).append(" Zona: ").append(validarObjetoNulo(vVuslSigla)).append(ESPACO_5).append(" Sala: ").append(validarObjetoNulo(vVuslSala)).append(char10).append("Consulta: ").append(validarObjetoNulo(vCaaDescricao)).append(" para ").append(validarObjetoNulo(vEspNomeReduzido)).append(char10).append("Equipe: ").append(validarObjetoNulo(vEqpNome)).append(" Profissional: ").append(validarObjetoNulo(vRapNome1)).append(char10).append("Paciente: ").append(vConPacCodigo).append(ESPACO_5).append(" Prontuário: ").append(validarObjetoNulo(vPacProntuario)).append("  -  ").append(char10).append(validarObjetoNulo(vPacNome2)).append(char10).append("Grade: ").append(validarObjetoNulo(vGrdGrade)).append(ESPACO_5).append(" Matricula: ").append(validarObjetoNulo(vConVinc)).append(ESPACO).append(validarObjetoNulo(vConSerMatriculaConsultado)).append(validarObjetoNulo(vCctCCusto)).append(char10).append(validarObjetoNulo(vConTpsTipo2)).append(ESPACO_5).append(" Marcado por: ").append(validarObjetoNulo(vUser));
			}else{
				
				vTexto.append("Nro. Consulta: ").append(validarObjetoNulo(vConNumero)).append("  ").append(" Data: ").append(validarObjetoNulo(vConDtConsulta)).append("  ").append(" Hora: ").append(validarObjetoNulo(vConDtConsulta1)).append("  ").append(" Dia: ").append(validarObjetoNulo(vConTpsTipo1)).append("  ").append(" Zona: ").append(validarObjetoNulo(vVuslSigla)).append("  ").append(" Sala: ").append(validarObjetoNulo(vVuslSala)).append(char10).append(char10).append(char10).append("Consulta: ").append(vCaaDescricao).append(ESPACO_6).append(" para ").append(vEspNomeReduzido).append(char10).append(char10).append(" Equipe: ").append(vEqpNome).append(ESPACO_6).append(" Profissional: ").append(vRapNome1).append(char10).append(char10).append("Paciente: ").append(validarObjetoNulo(vConPacCodigo)).append(ESPACO_6).append(" Prontuário: ").append(validarObjetoNulo(vPacProntuario)).append("  -  ").append(validarObjetoNulo(vPacNome2)).append(char10).append(char10).append("Grade: ").append(validarObjetoNulo(vGrdGrade)).append(ESPACO_6).append(" Matricula: ").append(validarObjetoNulo(vConVinc)).append(ESPACO).append(validarObjetoNulo(vConSerMatriculaConsultado)).append(validarObjetoNulo(vCctCCusto)).append(char10).append(char10).append(validarObjetoNulo(vConTpsTipo2)).append(ESPACO_6).append("Marcado por: ").append(validarObjetoNulo(vUser));
			}
		}
		return vTexto.toString();
	}
	
	/**
	 * #43033 P2
	 * @ORADB: AACC_BUSCA_NOME_PROF
	 * @param pMatricula
	 * @param pVinCodigo
	 * @return
	 */
	public String aacBuscaNomeProf(Integer pMatricula, Short pVinCodigo){
		
		String vNomeProfissional = " ";
		
		if(pMatricula != null){
			
			List<CursorCurPreVO> listaCurPreVO = this.registroColaboradorFacade.obterCursorCurRapVO(pMatricula, pVinCodigo);
			
			if(!listaCurPreVO.isEmpty()){
				if(listaCurPreVO.get(0).getNome() != null){
					vNomeProfissional = listaCurPreVO.get(0).getNome();
				}
			}
		}
		return vNomeProfissional;
	}
	
	private String validarObjetoNulo(Object object){
		if (object == null) {
			return "";
		} else {
			return object.toString();
		}
	}
	
	/**
	 * Validar Assinatura Médico
	 * @param seq
	 */
	public RelatorioConsultoriaAmbulatorialVO obterDadosConsultariaAmbulatorial(Long seq, Short espSeq) throws ApplicationBusinessException {	
		 
		MamInterconsultas cInterconsultas = null;		
		
		if (seq != null) {			
			cInterconsultas = mamInterconsultasDAO.obterDadosRelConsultoriasAmbulatoriais(seq);
		}		
		
		if (cInterconsultas != null) {
			validaCInterconsultas(cInterconsultas, espSeq);
		}
		
		return cConsultoriaAmbulatorial;
	}
	
	/**
	 * Validar Lista Médico
	 * @param listaMedico
	 */
	private ConselhoProfissionalServidorVO validarListaMedico(List<ConselhoProfissionalServidorVO> listaMedico){
		ConselhoProfissionalServidorVO conselhoProfissionalServidor = new ConselhoProfissionalServidorVO();

		if (!listaMedico.isEmpty()){
		
			if (listaMedico.get(0).getNome() != null){
				conselhoProfissionalServidor.setNome(listaMedico.get(0).getNome());
			}
			
			if (listaMedico.get(0).getSiglaConselho() != null){
				conselhoProfissionalServidor.setSiglaConselho(listaMedico.get(0).getSiglaConselho());
			}
			
			if (listaMedico.get(0).getNumeroRegistroConselho() != null){
				conselhoProfissionalServidor.setNumeroRegistroConselho(listaMedico.get(0).getNumeroRegistroConselho());
			}	
		}
		
		return conselhoProfissionalServidor;		
	}

	/**
	 * Validar Assinatura Médico
	 * @param conselhoProfissionalServidor
	 */
	private String validaAssinaturaMedico(ConselhoProfissionalServidorVO conselhoProfissionalServidor){
			
		return conselhoProfissionalServidor.getNome() != null ? conselhoProfissionalServidor.getNome() : " " 
				+ conselhoProfissionalServidor.getSiglaConselho() != null ? TRACO_SEPARADOR + conselhoProfissionalServidor.getSiglaConselho() : " "
				+ conselhoProfissionalServidor.getNumeroRegistroConselho() != null ? " " + conselhoProfissionalServidor.getNumeroRegistroConselho() : "";
	}
	

	/**
	 * Validar Interconsultas
	 * @param cInterconsultas
	 */
	private void validaCInterconsultasII (MamInterconsultas cInterconsultas){
		
		if (cInterconsultas.getEspecialidadeAgenda() != null && cInterconsultas.getEspecialidadeAgenda().getNomeEspecialidade() != null) {
			cConsultoriaAmbulatorial.setEspecialidadeAgenda(cInterconsultas.getEspecialidadeAgenda().getNomeEspecialidade());
		}
		
		if (cInterconsultas.getEquipe() != null && cInterconsultas.getEquipe().getNome() != null) {
			cConsultoriaAmbulatorial.setEquipe(cInterconsultas.getEquipe().getNome());
		}
		
		if (cInterconsultas.getvMamPessoaServidores() != null && cInterconsultas.getvMamPessoaServidores().getPesNome() != null) {
			cConsultoriaAmbulatorial.setNomeProfissional(cInterconsultas.getvMamPessoaServidores().getPesNome());
		}
		
		if (cInterconsultas.getObservacao() != null) {
			cConsultoriaAmbulatorial.setObservacao(cInterconsultas.getObservacao());
		}	
		
		if (cInterconsultas.getIndUrgente() != null) {
			cConsultoriaAmbulatorial.setUrgente(cInterconsultas.getIndUrgente());
		}
		
		if(cInterconsultas.getObservacaoAdicional() != null){
			cConsultoriaAmbulatorial.setObservacaoAdicional(cInterconsultas.getObservacaoAdicional());
		}
	}
	
	/**
	 * @param cInterconsultas
	 * @throws ApplicationBusinessException
	 */
	//Metodo utilizado para validar a variavel cInterconsultas
	private void validaCInterconsultas (MamInterconsultas cInterconsultas, Short espSeq) throws ApplicationBusinessException{
		List<ConselhoProfissionalServidorVO> listaMedico = null;
		StringBuffer sEspecialidadeMedico = new StringBuffer(200);		
		
		if(cInterconsultas.getServidorValida() != null){
			sEspecialidadeMedico.append(manterReceituarioON.obterEspecialidade(espSeq));
			listaMedico = obterRegistroMedico(cInterconsultas.getServidorValida().getId().getMatricula(), 
					cInterconsultas.getServidorValida().getId().getVinCodigo());
		}		
		
		if (cInterconsultas.getSeq() != null) {
			cConsultoriaAmbulatorial.setSeq(cInterconsultas.getSeq());
		}
		
		if (sEspecialidadeMedico != null){
			cConsultoriaAmbulatorial.setEspecialidadeMedico(sEspecialidadeMedico.toString());
		}
		
		ConselhoProfissionalServidorVO conselhoProfissionalServidor = validarListaMedico(listaMedico);
		
		if (conselhoProfissionalServidor != null){
			cConsultoriaAmbulatorial.setNomeMedico(conselhoProfissionalServidor.getNome());
			cConsultoriaAmbulatorial.setSiglaConselhoMedico((conselhoProfissionalServidor.getSiglaConselho()));
			cConsultoriaAmbulatorial.setNumeroRegistroConselhoMedico(conselhoProfissionalServidor.getNumeroRegistroConselho());
			cConsultoriaAmbulatorial.setAssinaturaMedico(validaAssinaturaMedico(conselhoProfissionalServidor));
		}	
		
		if (cInterconsultas.getPaciente() != null && cInterconsultas.getPaciente().getNome() != null) {
			cConsultoriaAmbulatorial.setNomePaciente(cInterconsultas.getPaciente().getNome());
		}
		
		if (cInterconsultas.getEspecialidade() != null && cInterconsultas.getEspecialidade().getNomeEspecialidade() != null) {
			cConsultoriaAmbulatorial.setEspecialidade(cInterconsultas.getEspecialidade().getNomeEspecialidade());
		}
		
		if (sEspecialidadeMedico != null) {
			cConsultoriaAmbulatorial.setEspecialidadeMedico(sEspecialidadeMedico.toString());
		}
		
		validaCInterconsultasII(cInterconsultas);
	}
	
		/**
	 * Retorna o código do processo que representa a Interconsulta
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ANAM
	 * 
	 */
	public Short retornarCodigoProcessoInterConsultas() throws ApplicationBusinessException {
		Short codigoProcessoInterconsulta = 0;
		AghParametros parametroCodProcessoInterconsulta = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_INTERCONSULTA);
		if (parametroCodProcessoInterconsulta.getVlrNumerico() != null) {
			codigoProcessoInterconsulta = parametroCodProcessoInterconsulta.getVlrNumerico().shortValue();
		}
		return codigoProcessoInterconsulta;
	}
	
	public Boolean verificarInterconsultas(AacConsultas consulta, Boolean vTemp, Boolean verificarProcesso) throws ApplicationBusinessException {
		List<MamInterconsultas> listaInterconsultas = mamInterconsultasDAO.obterInterconsultasPeloNumeroConsultaPendente(consulta.getNumero());
        for (MamInterconsultas solicInterconsultas : listaInterconsultas) {
	        if (solicInterconsultas.getPendente().equals(DominioIndPendenteAmbulatorio.V) 
	        		|| (solicInterconsultas.getPendente().equals(DominioIndPendenteAmbulatorio.P) 
	        				&& (!verificarProcesso || validarProcesso(null, null, this.retornarCodigoProcessoInterConsultas())))) {
	        	vTemp = Boolean.TRUE;
		}
	}

        return vTemp;
	}
		/**
	 * @param conNumero
	 * @param listaDocumentos
	 * @param vProntoAtendimento
	 * @param espSeq
	 * @throws ApplicationBusinessException
	 */
	private void obterListaDocumentosInterConsulta(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Boolean vProntoAtendimento, Short espSeq) throws ApplicationBusinessException {
		List<RelatorioConsultoriaAmbulatorialVO> relatorioConsultoriaAmbulatorial = new ArrayList<RelatorioConsultoriaAmbulatorialVO>();
		DocumentosPacienteVO documentoInterconsultas = null;		
		List<MamInterconsultas> listaInterconsutas = mamInterconsultasDAO.obterInterconsultasPeloNumeroConsultaPendente(conNumero);
		for (MamInterconsultas interconsultas : listaInterconsutas) {
			List<MamInterconsultas> lista = mamInterconsultasDAO.pesquisarDocumentoImpressoInterconsultaSituacao(conNumero);
			if (!lista.isEmpty()) {
				boolean imprimiu = false;
				if (interconsultas.getIndImpresso().equals("S")) {
					imprimiu = true;
				}
				documentoInterconsultas = setarDadosDocumentoPaciente("Interconsultas", imprimiu);
				if (lista.size() > 1) {
					documentoInterconsultas.setDescricao("Consultoria - " + lista.size());
				} else {
					documentoInterconsultas.setDescricao("Consultoria");
				}
				RelatorioConsultoriaAmbulatorialVO relatorio = obterDadosConsultariaAmbulatorial(interconsultas.getSeq(), espSeq);
				if (relatorio != null) {
					relatorioConsultoriaAmbulatorial.add(relatorio);
				}
			}
		}

		if (documentoInterconsultas != null) {
			documentoInterconsultas.setConsultoriaAmbulatorial(relatorioConsultoriaAmbulatorial);
			listaDocumentos.add(documentoInterconsultas);
		}
	}
	

//	/**
//	 * Validar Interconsultas
//	 * @param cInterconsultas
//	 */
//	private void validaCInterconsultasII (MamInterconsultas cInterconsultas){
//		
//		if (cInterconsultas.getEspecialidadeAgenda() != null && cInterconsultas.getEspecialidadeAgenda().getNomeEspecialidade() != null) {
//			cConsultoriaAmbulatorial.setEspecialidadeAgenda(cInterconsultas.getEspecialidadeAgenda().getNomeEspecialidade());
//		}
//		
//		if (cInterconsultas.getEquipe() != null && cInterconsultas.getEquipe().getNome() != null) {
//			cConsultoriaAmbulatorial.setEquipe(cInterconsultas.getEquipe().getNome());
//		}
//		
//		if (cInterconsultas.getvMamPessoaServidores() != null && cInterconsultas.getvMamPessoaServidores().getPesNome() != null) {
//			cConsultoriaAmbulatorial.setNomeProfissional(cInterconsultas.getvMamPessoaServidores().getPesNome());
//		}
//		
//		if (cInterconsultas.getObservacao() != null) {
//			cConsultoriaAmbulatorial.setObservacao(cInterconsultas.getObservacao());
//		}	
//		
//		if (cInterconsultas.getIndUrgente() != null) {
//			cConsultoriaAmbulatorial.setUrgente(cInterconsultas.getIndUrgente());
//		}
//		
//		if(cInterconsultas.getObservacaoAdicional() != null){
//			cConsultoriaAmbulatorial.setObservacaoAdicional(cInterconsultas.getObservacaoAdicional());
//		}
//	}
	
//	/**
//	 * @param cInterconsultas
//	 * @throws ApplicationBusinessException
//	 */
//	//Metodo utilizado para validar a variavel cInterconsultas
//	private void validaCInterconsultas (MamInterconsultas cInterconsultas) throws ApplicationBusinessException{
//		List<ConselhoProfissionalServidorVO> listaMedico = null;
//		StringBuffer sEspecialidadeMedico = new StringBuffer(200);		
//		
//		if(cInterconsultas.getServidorValida() != null){
//			sEspecialidadeMedico.append(manterReceituarioON.obterEspecialidade(cInterconsultas.getServidorValida().
//					getId().getMatricula(),	cInterconsultas.getServidorValida().getId().getVinCodigo()));
//			listaMedico = obterRegistroMedico(cInterconsultas.getServidorValida().getId().getMatricula(), 
//					cInterconsultas.getServidorValida().getId().getVinCodigo());
//		}		
//		
//		if (cInterconsultas.getSeq() != null) {
//			cConsultoriaAmbulatorial.setSeq(cInterconsultas.getSeq());
//		}
//		
//		if (sEspecialidadeMedico != null){
//			cConsultoriaAmbulatorial.setEspecialidadeMedico(sEspecialidadeMedico.toString());
//		}
//		
//		ConselhoProfissionalServidorVO conselhoProfissionalServidor = validarListaMedico(listaMedico);
//		
//		if (conselhoProfissionalServidor != null){
//			cConsultoriaAmbulatorial.setNomeMedico(conselhoProfissionalServidor.getNome());
//			cConsultoriaAmbulatorial.setSiglaConselhoMedico((conselhoProfissionalServidor.getSiglaConselho()));
//			cConsultoriaAmbulatorial.setNumeroRegistroConselhoMedico(conselhoProfissionalServidor.getNumeroRegistroConselho());
//			cConsultoriaAmbulatorial.setAssinaturaMedico(validaAssinaturaMedico(conselhoProfissionalServidor));
//		}	
//		
//		if (cInterconsultas.getPaciente() != null && cInterconsultas.getPaciente().getNome() != null) {
//			cConsultoriaAmbulatorial.setNomePaciente(cInterconsultas.getPaciente().getNome());
//		}
//		
//		if (cInterconsultas.getEspecialidade() != null && cInterconsultas.getEspecialidade().getNomeEspecialidade() != null) {
//			cConsultoriaAmbulatorial.setEspecialidade(cInterconsultas.getEspecialidade().getNomeEspecialidade());
//		}
//		
//		if (sEspecialidadeMedico != null) {
//			cConsultoriaAmbulatorial.setEspecialidadeMedico(sEspecialidadeMedico.toString());
//		}
//		
//		validaCInterconsultasII(cInterconsultas);
//	}
	
//		/**
//	 * Retorna o código do processo que representa a Interconsulta
//	 * 
//	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ANAM
//	 * 
//	 */
//	public Short retornarCodigoProcessoInterConsultas() throws ApplicationBusinessException {
//		Short codigoProcessoInterconsulta = 0;
//		AghParametros parametroCodProcessoInterconsulta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_INTERCONSULTA);
//		if (parametroCodProcessoInterconsulta.getVlrNumerico() != null) {
//			codigoProcessoInterconsulta = parametroCodProcessoInterconsulta.getVlrNumerico().shortValue();
//		}
//		return codigoProcessoInterconsulta;
//	}
//	
//		public String retornarParametroImprimeInterconsultas() throws ApplicationBusinessException {
//		// Busca parametro que indica se a Interconsulta deve ser impressa
//		String vInterconsultas = null;
//		AghParametros parametroInterconsulta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_IMPRIME_INTERCONSULTA);
//		if (parametroInterconsulta.getVlrTexto() != null) {
//			vInterconsultas = parametroInterconsulta.getVlrTexto();
//		}
//		return vInterconsultas;
//	}

//		/**
//	 * @param conNumero
//	 * @param listaDocumentos
//	 * @param vProntoAtendimento
//	 * @param espSeq
//	 * @throws ApplicationBusinessException
//	 */
//	private void obterListaDocumentosInterConsulta(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, Boolean vProntoAtendimento, Short espSeq) throws ApplicationBusinessException {
//		List<RelatorioConsultoriaAmbulatorialVO> relatorioConsultoriaAmbulatorial = new ArrayList<RelatorioConsultoriaAmbulatorialVO>();
//		DocumentosPacienteVO documentoInterconsultas = null;		
//		List<MamInterconsultas> listaInterconsutas = getMamInterconsultasDAO().obterInterconsultasPeloNumeroConsultaPendente(conNumero);
//		for (MamInterconsultas interconsultas : listaInterconsutas) {
//			List<MamInterconsultas> lista = mamInterconsultasDAO.pesquisarDocumentoImpressoInterconsultaSituacao(conNumero);
//			if (!lista.isEmpty()) {
//				boolean imprimiu = false;
//				if (interconsultas.getIndImpresso().equals("S")) {
//					imprimiu = true;
//				}
//				documentoInterconsultas = setarDadosDocumentoPaciente("Interconsultas", imprimiu);
//				if (lista.size() > 1) {
//					documentoInterconsultas.setDescricao("Consultoria - " + lista.size());
//				} else {
//					documentoInterconsultas.setDescricao("Consultoria");
//				}
//				RelatorioConsultoriaAmbulatorialVO relatorio = obterDadosConsultariaAmbulatorial(interconsultas.getSeq());
//				if (relatorio != null) {
//					relatorioConsultoriaAmbulatorial.add(relatorio);
//				}
//			}
//		}
//
//		if (documentoInterconsultas != null) {
//			documentoInterconsultas.setConsultoriaAmbulatorial(relatorioConsultoriaAmbulatorial);
//			listaDocumentos.add(documentoInterconsultas);
//		}
//	}

	public MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO(){
		return mamNotaAdicionalEvolucoesDAO;
	}
	
	protected AtendimentoPacientesAgendadosRN getAtendimentoPacientesAgendadosRN(){
		return atendimentoPacientesAgendadosRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO(){
		return mamAnamnesesDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO(){
		return mamEvolucoesDAO;
	}
	
	protected MamInterconsultasDAO getMamInterconsultasDAO(){
		return mamInterconsultasDAO;
	}
	
	protected MamReceituariosDAO getMamReceituariosDAO(){
		return mamReceituariosDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}	
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}	
		
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	protected MamTipoReceitProcessosDAO getMamTipoReceitProcessosDAO(){

		return mamTipoReceitProcessosDAO;
	}	
	
	protected MarcacaoConsultaRN getMarcacaoConsultaRN(){
		return marcacaoConsultaRN;
	}
	
	protected VMamProcXCidDAO getVMamProcXCidDAO() {
		return vMamProcXCidDAO;
	}	
	
	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}		
	
	protected MamSituacaoAtendimentosDAO getMamSituacaoAtendimentosDAO() {
		return mamSituacaoAtendimentosDAO;
	}

	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO(){
		return mamItemAnamnesesDAO;
	}
	
	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO(){
		return mamItemEvolucoesDAO;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AacConsultasDAO getAacConsultasDAO(){
		return aacConsultasDAO;
	}
	
	protected MamAtestadosDAO getMamAtestadosDao(){
		return mamAtestadosDAO;
	}
}
