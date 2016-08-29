package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.dao.AacAtendimentoApacsDAO;
import br.gov.mec.aghu.controleinfeccao.vo.HistoricoAcomodacaoJnVO;
import br.gov.mec.aghu.controleinfeccao.vo.HistoricoSalaJnVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioControleFrequenciaSituacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.farmacia.dao.VAfaDescrMdtoDAO;
import br.gov.mec.aghu.model.AfaDiluicaoMdto;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaJn;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptCidTratTerapeutico;
import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.model.MptControleFrequencia;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptFavoritosServidor;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptItemCuidadoSumario;
import br.gov.mec.aghu.model.MptItemMdtoSumario;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptParamCalculoPrescricao;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptSessaoExtra;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoJustificativa;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioItensPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.TratamentoTerapeuticoVO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MptProtocoloCuidadosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmDosagemDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.AfaDiluicaoMdtoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendaPrescricaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptBloqueioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCidTratTerapeuticoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleFreqSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptDataItemSumarioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptDiaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptExtratoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptFavoritoServidorDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptFavoritosServidorDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptIntercorrenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptItemCuidadoSumarioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptItemMdtoSumarioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptItemPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptJustificativaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptParamCalculoDosesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptParamCalculoPrescricaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoCuidadoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoPacienteDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProfCredAssinatLaudoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloAssociacaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCuidadosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloItemMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoExtraDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoIntercorrenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoIntercorrenciaJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoJustificativaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoTipoIntercorDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTratamentoTerapeuticoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptVersaoProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AcomodacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AfaFormaDosagemVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentosPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AlterarHorariosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ConsultaDadosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiaPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HistoricoSessaoJnVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HomologarProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.JustificativaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaAfaDescMedicamentoTipoUsoMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaEsperaRetirarPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ManterFavoritosUsuarioVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaJnVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.NovaVersaoProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacientesTratamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PercentualOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacientePTVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProcedimentosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistrarControlePacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TaxaOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TransferirDiaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.VincularIntercorrenciaTipoSessaoVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloAssociadoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloSessaoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.SituacaoVersaoProtocoloVO;
import br.gov.mec.aghu.view.VMpmDosagem;


@Stateless
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
public class ProcedimentoTerapeuticoFacade extends BaseFacade implements IProcedimentoTerapeuticoFacade {
	 
	private static final long serialVersionUID = 8341931086608875042L;

	@EJB
	private ProcedimentoTerapeuticoON procedimentoTerapeuticoON;
	@EJB
	private MptTipoSessaoRN mptTipoSessaoRN;
	@EJB
	private MptHorarioSessaoRN mptHorarioSessaoRN;
	@EJB
	private MptSalasRN mptSalasRN;
	@EJB
	private MptSessaoRN mptSessaoRN;
	@EJB
	private MptLocalAtendimentoRN mptLocalAtendimentoRN;		
	@EJB
	private MptDiaTipoSessaoRN mptDiaTipoSessaoRN;
	@EJB
	private MptTurnoTipoSessaoRN mptTurnoTipoSessaoRN;
	@EJB
	private AgendamentoSessaoON agendamentoSessaoON;
	@EJB
	private AgendamentoSessaoRN agendamentoSessaoRN;
	@EJB
	private AgendaDetalhadaRN agendaDetalhadaRN;
	@EJB
	private MapaDisponibilidadeRN mapaDisponibilidadeRN;
	@EJB
	private MpaProtocoloAssistencialRN protocoloAssistencialRN;
	@EJB
	private MptTratamentoTerapeuticoRN mptTratamentoTerapeuticoRN;
	
	@EJB
	private MptProtocoloCicloRN mptProtocoloCicloRN;
	@Inject	
	private MptPrescricaoPacienteDAO mptPrescricaoPacienteDAO;
	@Inject
	private MptDataItemSumarioDAO mptDataItemSumarioDAO;
	@Inject
	private MptAgendaPrescricaoDAO mptAgendaPrescricaoDAO;
	@Inject	
	private MptParamCalculoPrescricaoDAO mptParamCalculoPrescricaoDAO;
	@Inject	
	private MptControleFreqSessaoDAO mptControleFreqSessaoDAO;
	@Inject	
	private MptTratamentoTerapeuticoDAO mptTratamentoTerapeuticoDAO;
	@Inject	
	private MptCidTratTerapeuticoDAO mptCidTratTerapeuticoDAO;
	@Inject	
	private MptProfCredAssinatLaudoDAO mptProfCredAssinatLaudoDAO;
	@Inject	
	private MptPrescricaoCuidadoDAO mptPrescricaoCuidadoDAO;
	@Inject	
	private MptPrescricaoMedicamentoDAO mptPrescricaoMedicamentoDAO;
	@Inject	
	private MptItemPrescricaoMedicamentoDAO mptItemPrescricaoMedicamentoDAO;
	@Inject		
	private MptItemPrescricaoSumarioDAO mptItemPrescricaoSumarioDAO;
	@Inject		
	private MptItemCuidadoSumarioDAO mptItemCuidadoSumarioDAO;
	@Inject		
	private MptItemMdtoSumarioDAO mptItemMdtoSumarioDAO;
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	@Inject
	private MptSalasDAO mptSalasDAO;
	@Inject
	private MptSalasJnDAO mptDalasJnDAO;
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;
	@Inject
	private MptLocalAtendimentoJnDAO mptLocalAtendimentoJnDAO;
	@Inject
	private MptDiaTipoSessaoDAO mptDiaTipoSessaoDAO;
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	@Inject
	private MptFavoritoServidorDAO mptFavoritoServidorDAO;
	@Inject
	private MptTipoSessaoJnDAO mptTipoSessaoJnDAO;
	@Inject
	private MptFavoritoServidorDAO mptServidorFavoritoDAO;
	@Inject
	private MptBloqueioDAO mptBloqueioDAO;
	@EJB
	private MptFavoritoServidorRN mptFavoritoServidorRN;
	@Inject
	private MptSessaoDAO mptSessaoDAO;
	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;
	@Inject
	private MptJustificativaDAO mptJustificativaDAO;
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;
	@Inject
	private MptSessaoExtraDAO MptSessaoExtraDAO;
	@Inject
	private MptExtratoSessaoDAO MptExtratoSessaoDAO;
	@EJB
	private ManutencaoAgendamentoSessaoTerapeuticaRN manutencaoAgendamentoSessaoTerapeuticaRN;
	@EJB
	private ManutencaoAgendamentoSessaoTerapeuticaON manutencaoAgendamentoSessaoTerapeuticaON;	
	@EJB
	private ImprimirTicketAgendamentoRN imprimirTicketAgendamentoRN;	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	@Inject
	private MptProtocoloSessaoRN mptProtocoloSessaoRN;
	
	@Inject
	private MptProtocoloSessaoDAO mptProtocoloSessaoDAO;
	
	@EJB
	private ProcedimentoTerapeuticoRN procedimentoTerapeuticoRN;
	@EJB
	private MptAgendamentoSessaoRN mptAgendamentoSessaoRN;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private MptFavoritosServidorDAO mptFavoritosServidorDAO;
	
	@Inject
	private MptVersaoProtocoloSessaoDAO mptVersaoProtocoloSessaoDAO;
	
	@Inject
	private MptCaracteristicaJnDAO mptCaracteristicaJnDAO;
	
	@Inject
	private MptTipoIntercorrenciaDAO mptTipoIntercorrenciaDAO;
	
	@Inject
	private MptTipoIntercorrenciaJnDAO mptTipoIntercorrenciaJnDAO;
	
	@Inject
	private MptTipoSessaoTipoIntercorDAO mptTipoSessaoTipoIntercorDAO;
	
	@Inject
	private MptTipoIntercorrenciaRN mptTipoIntercorrenciaRN;
	
	@EJB
	private MptTipoSessaoTipoIntercorRN mptTipoSessaoTipoIntercorRN;
	
	@Inject
 	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;
	
	@EJB
	private MptCaracteristicaTipoSessaoON mptCaracteristicaTipoSessaoON;
	
	@EJB
	private MptCaracteristicaON mptCaracteristicaON;
	
	@Inject
	private MptCaracteristicaDAO mptCaracteristicaDAO;
	
	@Inject
	private AacAtendimentoApacsDAO aacAtendimentoApacsDAO;
	
	@Inject
	private MptTipoJustificativaDAO mptTipoJustificativaDAO;
	
	@EJB
	private ManterTipoJustificativaON manterTipoJustificativaON;
	@Inject
	private MptControleFrequenciaRN mptControleFrequenciaRN;
	
	@Inject
	private MptIntercorrenciaDAO mptIntercorrenciaDAO;
	
	
	@Inject
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;
	
	@Inject
	private AfaViaAdministracaoDAO afaViaAdministracaoDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
		
	@Inject
	private AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO;
	
	@EJB
	private MptProtocoloMedicamentoRN mptProtocoloMedicamentoRN;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MptProtocoloCuidadosDAO mptProtocoloCuidadosDAO;
	
	@Inject
	private MptProtocoloItemMedicamentosDAO mptProtocoloItemMedicamentosDAO;
	
	@Inject
	private MptProtocoloMedicamentosDiaDAO mptProtocoloMedicamentosDiaDAO;
	
	@Inject
	private VAfaDescrMdtoDAO vAfaDescrMdtoDAO;

	@Inject
	private VMpmDosagemDAO vMpmDosagemDAO;
	
	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private AfaDiluicaoMdtoDAO afaDiluicaoMdtoDAO;
	
	@Inject
	private MptProtocoloAssociacaoDAO mptProtocoloAssociacaoDAO;

	@Inject
	private MptProtocoloCuidadosDiaDAO mptProtocoloCuidadosDiaDAO;
		
	@EJB
	private CadastroSolucaoProtocoloRN cadastroSolucaoProtocoloRN;
	
	@Inject
	private MptParamCalculoDosesDAO mptParamCalculoDosesDAO;
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade#listarAgendasPrescricaoPorNumeroConsulta(java.lang.Integer)
	 */
	@Override
	public List<MptAgendaPrescricao> listarAgendasPrescricaoPorNumeroConsulta(Integer pConNumero) {
		return this.getMptAgendaPrescricaoDAO().listarAgendasPrescricaoPorNumeroConsulta(
			pConNumero);
	}
	@Override	
	public void atualizarMptAgendaPrescricao(MptAgendaPrescricao mptAgendaPrescricao){
		getMptAgendaPrescricaoDAO().atualizar(mptAgendaPrescricao);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade#obterReservasPacienteParaConfirmacaoCancelamento(java.lang.Integer, java.util.List)
	 */
	@Override
	public List<HorarioReservadoVO> obterReservasPacienteParaConfirmacaoCancelamento(Integer pacCodigo, List<CadIntervaloTempoVO> listaHorarios) {

		return getMptAgendamentoSessaoRN().obterReservasPacienteParaConfirmacaoCancelamento(pacCodigo, listaHorarios);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade#cancelarReservas(java.util.List)
	 */
	@Override
	public void cancelarReservas(List<HorarioReservadoVO> horariosReservados) {

		mptHorarioSessaoRN.cancelarReservas(horariosReservados);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade#confirmarReservas(java.util.List, java.lang.Short, java.lang.Short,
	 *  java.lang.Short, java.lang.Integer, java.lang.String, java.util.List)
	 */
	@Override
	public void confirmarReservas(List<HorarioReservadoVO> horariosReservados, Short tpsSeq, Short salaSeq, Short espSeq, Integer prescricao,
			String nomeMicrocomputador, List<CadIntervaloTempoVO> listaPrescricoes) throws BaseException {

		mptHorarioSessaoRN.confirmarReservas(horariosReservados, tpsSeq, salaSeq, espSeq, prescricao, nomeMicrocomputador, listaPrescricoes);
	}

	protected ProcedimentoTerapeuticoON getProcedimentoTerapeuticoON() {
		return procedimentoTerapeuticoON;
	}	
	protected MptPrescricaoPacienteDAO getMptPrescricaoPacienteDAO() {
		return mptPrescricaoPacienteDAO;
	}
	protected MptDataItemSumarioDAO getMptDataItemSumarioDAO() {
		return mptDataItemSumarioDAO;
	}
	protected MptAgendaPrescricaoDAO getMptAgendaPrescricaoDAO() {
		return mptAgendaPrescricaoDAO;
	}
	
	protected MptParamCalculoPrescricaoDAO getMptParamCalculoPrescricaoDAO() {
		return mptParamCalculoPrescricaoDAO;
	}
	
	protected MptControleFreqSessaoDAO getMptControleFreqSessaoDAO() {
		return mptControleFreqSessaoDAO;
	}
	
	protected MptTratamentoTerapeuticoDAO getMptTratamentoTerapeuticoDAO(){
		return mptTratamentoTerapeuticoDAO;
	}
	protected MptCidTratTerapeuticoDAO getMptCidTratamentoTerapeuticoDAO(){
		return mptCidTratTerapeuticoDAO;
	}

	protected MptAgendamentoSessaoRN getMptAgendamentoSessaoRN() {
		return mptAgendamentoSessaoRN;
	}

	@Override
	@BypassInactiveModule
	public List<MptPrescricaoPaciente> listarPeriodosSessoesQuimio(Integer apaAtdSeq) {
		return getMptPrescricaoPacienteDAO().listarPeriodosSessoesQuimio(apaAtdSeq);
	}
	@Override
	@BypassInactiveModule
	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorPacienteTptSeq(Integer codPaciente, Integer tptSeq) {
		return getMptTratamentoTerapeuticoDAO().listarTratamentosTerapeuticosPorPacienteTptSeq(codPaciente, tptSeq);
	}
	@Override
	@BypassInactiveModule
	public List<MptTratamentoTerapeutico> listarSessoesFisioterapia(Integer codPaciente, Integer tptSeq) {
		return getMptTratamentoTerapeuticoDAO().listarSessoesFisioterapia(codPaciente, tptSeq);
	}	
	@Override
	@BypassInactiveModule
	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return getMptControleFreqSessaoDAO().listarSessoesFisioterapia(trpSeq, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	@BypassInactiveModule
	public Long listarSessoesFisioterapiaCount(Integer trpSeq){
		return getMptControleFreqSessaoDAO().listarSessoesFisioterapiaCount(trpSeq);
	}
	@Override
	@BypassInactiveModule
	public List<MptTratamentoTerapeutico> pesquisarTratamentosTerapeuticosPorPaciente(
			AipPacientes paciente) {
		return this.getMptTratamentoTerapeuticoDAO()
				.pesquisarTratamentosTerapeuticosPorPaciente(paciente);
	}
	@Override
	@BypassInactiveModule
	public Long pesquisarTratamentosTerapeuticosPorPacienteCount(
			AipPacientes paciente) {
		return this.getMptTratamentoTerapeuticoDAO()
				.pesquisarTratamentosTerapeuticosPorPacienteCount(paciente);
	}
	@Override
	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getMptTratamentoTerapeuticoDAO()
				.listarTratamentosTerapeuticosPorCodigoPaciente(pacCodigo);
	}
	@Override
	public void persistirMptTratamentoTerapeutico(
			MptTratamentoTerapeutico mptTratamentoTerapeutico) {
		this.getMptTratamentoTerapeuticoDAO().persistir(
				mptTratamentoTerapeutico);
	}
	@Override
	public void inserirMptTratamentoTerapeuticoDialise(Short unfSeqDialise, Short espSeqDialise, Integer pacCodigo,
			Integer tipoTratDialise, Short cspCnvCodigo, Byte cspSeq, Integer matriculaResp, Short vinCodigoResp, String nomeMicrocomputador) throws BaseException {
		this.mptTratamentoTerapeuticoRN.inserirMptTratamentoTerapeuticoDialise(unfSeqDialise, espSeqDialise, pacCodigo,
				tipoTratDialise, cspCnvCodigo, cspSeq, matriculaResp, vinCodigoResp, nomeMicrocomputador);
	}

	@Override
	public List<MptParamCalculoPrescricao> pesquisarMptParamCalculoPrescricoes(
			Integer pesoPacCodigo, Integer alturaPacCodigo) {
		return this.getMptParamCalculoPrescricaoDAO()
				.pesquisarMptParamCalculoPrescricoes(pesoPacCodigo,
						alturaPacCodigo);
	}
	@Override
	@BypassInactiveModule
	public Long listarProfCredAssinatLaudoCount(Integer matricula,
			Integer vinculo, Integer esp, Short convenio) {
		return this.getMptProfCredAssinatLaudoDAO()
				.listarProfCredAssinatLaudoCount(matricula, vinculo, esp,
						convenio);
	}
	protected MptProfCredAssinatLaudoDAO getMptProfCredAssinatLaudoDAO() {
		return mptProfCredAssinatLaudoDAO;
	}
	@Override
	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticos(
			Integer pacCodigo, AghAtendimentos atendimento) {
		return this.getMptTratamentoTerapeuticoDAO()
				.listarTratamentosTerapeuticos(pacCodigo, atendimento);
	}
	protected MptPrescricaoCuidadoDAO getMptPrescricaoCuidadoDAO(){
		return mptPrescricaoCuidadoDAO;
	}
	protected MptPrescricaoMedicamentoDAO getMptPrescricaoMedicamentoDAO(){
		return mptPrescricaoMedicamentoDAO;
	}
	protected MptItemPrescricaoMedicamentoDAO getMptItemPrescricaoMedicamentoDAO(){
		return mptItemPrescricaoMedicamentoDAO;
	}
	@Override
	@BypassInactiveModule
	public List<MptItemPrescricaoMedicamento> listarItensPrescricaoMedicamento(
			Integer atdSeq, Integer pteSeq, Integer pmoSeq) {
		return getMptItemPrescricaoMedicamentoDAO().listarItensPrescricaoMedicamento(atdSeq, pteSeq, pmoSeq);
	}
	@Override
	@BypassInactiveModule
	public MptTratamentoTerapeutico obterTratamentoTerapeutico(Integer seqTratamentoTerQuimio) {
		return getMptTratamentoTerapeuticoDAO().obterPorChavePrimaria(seqTratamentoTerQuimio, MptTratamentoTerapeutico.Fields.PACIENTE, MptTratamentoTerapeutico.Fields.SERVIDOR_RESPONSAVEL);
	}
	@Override
	@BypassInactiveModule
	public List<MptCidTratTerapeutico> listarCidTratamentoQuimio(
			Integer seqTratamentoTerQuimio) {
		return getMptCidTratamentoTerapeuticoDAO().pesquisarMptCidTratTerapeutico(seqTratamentoTerQuimio);
	}
	@Override
	@BypassInactiveModule
	public Integer obterPrescricaoPacienteporTrpSeq(Integer trpSeq) {
		return getMptPrescricaoPacienteDAO().obterPrescricaoPacienteporTrpSeq(trpSeq);
	}
	@Override
	@BypassInactiveModule
	public List<SumarioQuimioItensPOLVO> listarDadosPeriodosSumarioPrescricaoQuimio(
			Date dtInicial, Date dtFinal, Integer apaAtdSeq, Integer apaSeq) {
		return getMptDataItemSumarioDAO().listarDadosPeriodosSumarioPrescricaoQuimio(dtInicial, dtFinal, apaAtdSeq, apaSeq);
	}
	@Override
	@BypassInactiveModule
	public List<String> listarTituloProtocoloAssistencial(Integer atdSeq,
			Date dtInicio, Date dtFim) {
		return getMptPrescricaoPacienteDAO().listarTituloProtocoloAssistencial(atdSeq, dtInicio, dtFim);
	}
	@Override
	@BypassInactiveModule
	public List<MptPrescricaoPaciente> pesquisarPrescricoesPacientePorAtendimento(Integer atdSeq) {
		return getMptPrescricaoPacienteDAO().pesquisarPrescricoesPacientePorAtendimento(atdSeq);
	}
	@Override
	@BypassInactiveModule
	public List<MptPrescricaoCuidado> pesquisarPrescricoesCuidado(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda) {
		return getMptPrescricaoCuidadoDAO().pesquisarPrescricoesCuidado(pteAtdSeq, pteSeq, agpDtAgenda);
	}
	@Override
	@BypassInactiveModule
	public List<MptDataItemSumario> pesquisarDataItemSumario(Integer intAtdSeq, Integer apaSeq, Integer itqSeq) {
		return getMptDataItemSumarioDAO().pesquisarDataItemSumario(intAtdSeq, apaSeq, itqSeq);
	}
	//Precisa ter bypass nessa persistencia pq é usado na POL
	//No momento da implantacao 4.1 procedimento terapeutico está inativo e foi liberado nesse ponto. 
	@Override
	@BypassInactiveModule
	public void persistirMptDataItemSumario(MptDataItemSumario itemSum) {
		getMptDataItemSumarioDAO().persistir(itemSum);		
	}
	@Override
	@BypassInactiveModule
	public MptPrescricaoCuidado obterMptPrescricaoCuidadoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pcoSeq, Boolean join1, Boolean join2) {
		return getMptPrescricaoCuidadoDAO().obterMptPrescricaoCuidadoComJoin(pteAtdSeq, pteSeq, pcoSeq, join1, join2 );
	}
	@Override
	@BypassInactiveModule
	public List<MptItemPrescricaoSumario> pesquisarItemPrescricaoSumario(Integer intAtdSeq, Integer apaSeq, String sintaxe, DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum) {
		return getMptItemPrescricaoSumarioDAO().pesquisarItemPrescricaoSumario(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum);
	}
	protected MptItemPrescricaoSumarioDAO getMptItemPrescricaoSumarioDAO(){
		return mptItemPrescricaoSumarioDAO;
	}
	//Precisa ter bypass nessa persistencia pq é usado na POL
	//No momento da implantacao 4.1 procedimento terapeutico está inativo e foi liberado nesse ponto. 
	@Override
	@BypassInactiveModule
	public void persistirMptItemPrescricaoSumario(MptItemPrescricaoSumario itemPrescSum) {
		getMptItemPrescricaoSumarioDAO().persistir(itemPrescSum);			
	}
	//bypass pq é usado na POL
	@Override
	@BypassInactiveModule
	public void persistirMptItemCuidadoSumario(MptItemCuidadoSumario itemCuidSum) {
		getMptItemCuidadoSumarioDAO().persistir(itemCuidSum);
	}
	protected MptItemCuidadoSumarioDAO getMptItemCuidadoSumarioDAO(){ 
		return mptItemCuidadoSumarioDAO;
	}
	@Override
	@BypassInactiveModule
	public List<MptPrescricaoMedicamento> listarPrescricoesMedicamento(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, Boolean solucao) {
		return getMptPrescricaoMedicamentoDAO().listarPrescricoesMedicamento(pteAtdSeq, pteSeq, agpDtAgenda, solucao);
	}
	@Override
	@BypassInactiveModule
	public MptPrescricaoMedicamento obterMptPrescricaoMedicamentoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pmoSeq, Boolean solucao) {
		return getMptPrescricaoMedicamentoDAO().obterMptPrescricaoMedicamentoComJoin(pteAtdSeq, pteSeq, pmoSeq, solucao);
	}
	@Override
	@BypassInactiveModule
	public List<MptItemPrescricaoMedicamento> listarItensPrescricaoMdtoJoin(Integer atdSeq, Integer pteSeq, Integer pmoSeq) {
		return getMptItemPrescricaoMedicamentoDAO().listarItensPrescricaoMdtoJoin(atdSeq, pteSeq, pmoSeq);
	}
	//Precisa ter bypass nessa persistencia pq é usado na POL
	//No momento da implantacao 4.1 procedimento terapeutico está inativo e foi liberado nesse ponto. 
	@Override
	@BypassInactiveModule
	public void persistirMptItemMdtoSumario(MptItemMdtoSumario itemMdtoSum) {
		getMptItemMdtoSumarioDAO().persistir(itemMdtoSum);		
	}
	protected MptItemMdtoSumarioDAO getMptItemMdtoSumarioDAO(){ 
		return mptItemMdtoSumarioDAO;
	}
	@Override
	@BypassInactiveModule
	public List<MptPrescricaoMedicamento> listarPrescricaoMedicamentoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda, Boolean solucao) {
		return getMptPrescricaoMedicamentoDAO().listarPrescricaoMedicamentoHierarquia(pteAtdSeq, pteSeq, seq, agpDtAgenda, solucao);
	}
	@Override
	@BypassInactiveModule
	public List<MptPrescricaoCuidado> listarPrescricaoCuidadoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda) {
		return getMptPrescricaoCuidadoDAO().listarPrescricaoCuidadoHierarquia(pteAtdSeq, pteSeq, seq, agpDtAgenda); 
	}
	@Override
	public void atualizarAgendaPrescricaoPorCirurgiaCallableStatement(final Integer cirurgiaSeq) throws ApplicationBusinessException{
		getMptAgendaPrescricaoDAO().atualizarAgendaPrescricaoPorCirurgiaCallableStatement(cirurgiaSeq);
	}
	@Override
	public MptAgendaPrescricao obterDataAgendaPrescricaoAtiva(Integer crgSeq, Short unfSeq) {
		return getMptAgendaPrescricaoDAO().obterDataAgendaPrescricaoAtiva(crgSeq, unfSeq);
	}
	@Override
	public void atualizaMptAgendaPrescricao(MptAgendaPrescricao agendaPrescricao, boolean flush) throws ApplicationBusinessException {
		getMptAgendaPrescricaoDAO().atualizar(agendaPrescricao);
		if (flush){
			getMptAgendaPrescricaoDAO().flush();			
		}
	}	
	@Override
	public MptTratamentoTerapeutico pesquisarTratamentoTerapeuticoPorSeq(
			Integer tprSeq) {
		return this.getMptTratamentoTerapeuticoDAO().obterPorChavePrimaria(tprSeq);
	}
	@Override
	public TratamentoTerapeuticoVO buscarTratamentoTerapeuticoPorSeq(
			Integer trpSeq) {
		return this.getMptTratamentoTerapeuticoDAO().buscarTratamentoTerapeuticoPorSeq(trpSeq);
	}
	@Override
	public List<MptTipoSessao> listarMptTipoSessao(String descricao, Short unfSeq, DominioSituacao situacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.mptTipoSessaoDAO.listarMptTipoSessao(descricao, unfSeq, situacao, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long listarMptTipoSessaoCount(String descricao, Short unfSeq, DominioSituacao situacao) {
		return this.mptTipoSessaoDAO.listarMptTipoSessaoCount(descricao, unfSeq, situacao);
	}
	@Override
	public List<MptLocalAtendimento> listarMptLocaisAtendimentoPorSala(Short salaSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.mptLocalAtendimentoDAO.listarMptLocaisAtendimentoPorSala(salaSeq, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long listarMptLocaisAtendimentoPorSalaCount(Short salaSeq) {
		return this.mptLocalAtendimentoDAO.listarMptLocaisAtendimentoPorSalaCount(salaSeq);
	}	
	@Override
	public List<MptSalas> listarMptSalas(Short tpsSeq, Short espSeq, String descricao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.mptSalasDAO.listarMptSalas(tpsSeq, espSeq, descricao, firstResult, maxResult, orderProperty, asc);
	}
	@Override
	public Long listarMptSalasCount(Short tpsSeq, Short espSeq, String descricao) {
		return this.mptSalasDAO.listarMptSalasCount(tpsSeq, espSeq, descricao);
	}
	@Override
	public MptSalas obterMptSalaPorSeq(Short seq) {
		return this.mptSalasDAO.obterMptSalaPorSeq(seq);
	}
	@Override
	public void persistirMptSala(MptSalas mptSala) throws ApplicationBusinessException {
		this.mptSalasRN.persistirMptSala(mptSala);
	}
	@Override
	public void persistirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		this.mptLocalAtendimentoRN.persistirMptLocalAtendimento(mptLocalAtendimento);
	}		
	@Override
	public void excluirMptSala(MptSalas mptSala) throws ApplicationBusinessException {
		this.mptSalasRN.excluirMptSalas(mptSala);
	}
	@Override
	public void excluirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		this.mptLocalAtendimentoRN.excluirMptLocalAtendimento(mptLocalAtendimento);
	}		
	@Override
	public MptTipoSessao obterMptTipoSessaoPorSeq(Short seq) {
		return this.mptTipoSessaoDAO.obterMptTipoSessaoPorSeq(seq);
	}
	@Override
	public void inserirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		this.mptTipoSessaoRN.inserirMptTipoSessao(mptTipoSessao);
	}
	@Override
	public void atualizarMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		this.mptTipoSessaoRN.atualizarMptTipoSessao(mptTipoSessao);
	}
	@Override
	public void excluirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		this.mptTipoSessaoRN.excluirMptTipoSessao(mptTipoSessao);
	}
	@Override
	public List<MptTipoSessao> listarTiposSessao(String param) {
		return this.mptTipoSessaoDAO.listarTiposSessao(param);
	}
	@Override
	public Long listarTiposSessaoCount(String param) {
		return this.mptTipoSessaoDAO.listarTiposSessaoCount(param);
	}
	@Override
	public List<MptTipoSessao> listarTiposSessaoPorUnfSeq(Short unfSeq) {
		return this.mptTipoSessaoDAO.listarTiposSessaoPorUnfSeq(unfSeq);
	}
	@Override
	public List<MptSalas> listarSalas(Short tpsSeq, String param) {
		return this.mptSalasDAO.listarSalas(tpsSeq, param);
	}
	@Override
	public Long listarSalasCount(Short tpsSeq, String param) {
		return this.mptSalasDAO.listarSalasCount(tpsSeq, param);
	}
	@Override
	public void validarMptDiaTipoSessao(Boolean segunda, Boolean terca, Boolean quarta, Boolean quinta, Boolean sexta,
			Boolean sabado, Boolean domingo, Short tpsSeq) throws ApplicationBusinessException {
		this.mptDiaTipoSessaoRN.validarMptDiaTipoSessao(segunda, terca, quarta, quinta, sexta, sabado, domingo, tpsSeq);
	}
	@Override
	public List<MptDiaTipoSessao> obterDiasPorTipoSessao(Short tpsSeq) {
		return this.mptDiaTipoSessaoDAO.obterDiasPorTipoSessao(tpsSeq);
	}
	@Override
	public void validarConflitosPeriodo(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite) throws ApplicationBusinessException {
		this.mptTurnoTipoSessaoRN.validarConflitosPeriodo(manha, tarde, noite, horaInicialManha, horaFinalManha,
				horaInicialTarde,horaFinalTarde, horaInicialNoite, horaFinalNoite);
	}
	@Override
	public void validarMptTurnoTipoSessao(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite, Short tpsSeq) throws ApplicationBusinessException {
		this.mptTurnoTipoSessaoRN.validarMptTurnoTipoSessao(manha, tarde, noite, horaInicialManha, horaFinalManha,
				horaInicialTarde, horaFinalTarde, horaInicialNoite, horaFinalNoite, tpsSeq);
	}
	@Override
	public List<MptTurnoTipoSessao> obterTurnosPorTipoSessao(Short tpsSeq) {
		return this.mptTurnoTipoSessaoDAO.obterTurnosPorTipoSessao(tpsSeq);
	}
	@Override
	public List<PrescricaoPacienteVO> obterListaPrescricoesPorPaciente(Integer pacCodigo, Date dataCalculada) {
		return this.mptSessaoRN.obterListaPrescricoesPorPaciente(pacCodigo, dataCalculada);
	}
	@Override
	public List<CadIntervaloTempoVO> listarIntervalosTempoPrescricao(Integer lote, Integer cloSeq) {
		return this.mptSessaoRN.listarIntervalosTempoPrescricao(lote, cloSeq);
	}
	@Override
	public void validarFiltrosAgendamentoSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			DominioTurno turno, Date dataInicio, Date dataFim, Date dthrHoraInicio) throws ApplicationBusinessException {
		this.agendamentoSessaoON.validarFiltrosAgendamentoSessao(listaHorarios, tpsSeq, turno, dataInicio, dataFim, dthrHoraInicio);
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<HorariosAgendamentoSessaoVO> sugerirAgendaSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			Short salaSeq, Short espSeq, Integer pacCodigo, Integer prescricao, DominioTipoLocal acomodacao, DominioTurno turno,
			Date dataInicio, Date dataFim, Date dthrHoraInicio, String[] diasSelecionados, boolean isSugestao) throws ApplicationBusinessException {
		return this.agendamentoSessaoRN.sugerirAgendaSessao(listaHorarios, tpsSeq, salaSeq, espSeq,
				pacCodigo, prescricao, acomodacao, turno, dataInicio, dataFim, dthrHoraInicio, diasSelecionados, isSugestao);
	}
	@Override
	public List<PercentualOcupacaoVO> preencherPercentualOcupacao(Short salSeq, Short tpsSeq, List<HorariosAgendamentoSessaoVO> horariosAgendados) {
		return this.agendamentoSessaoRN.preencherPercentualOcupacao(salSeq, tpsSeq, horariosAgendados);
	}
	@Override
	public MptAgendamentoSessao agendarSessao(List<HorariosAgendamentoSessaoVO> horariosGerados, Short tpsSeq, Short salaSeq, DominioTurno turno,
			Integer pacCodigo, Short vpaPtaSeq, Short vpaSeqp, DominioTipoLocal acomodacao, Date dataInicio,
			Date dataFim, String nomeMicrocomputador) throws BaseException {
		return this.agendamentoSessaoRN.agendarSessao(horariosGerados, tpsSeq, salaSeq, turno, pacCodigo,
				vpaPtaSeq, vpaSeqp, acomodacao, dataInicio, dataFim, nomeMicrocomputador);
	}
	@Override
	public List<AcomodacaoVO> obterListaHorariosAgendadosPorAcomodacao(Short tpsSeq, Short salSeq, Date dataMapeamento) {
		return this.agendaDetalhadaRN.obterListaHorariosAgendadosPorAcomodacao(tpsSeq, salSeq, dataMapeamento);
	}
	@Override
	public List<HistoricoSalaJnVO> obterHistoricoSalaJn(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Short localSeq) {
		return mptSalasRN.obterHistoricoSalaJn(firstResult, maxResults, orderProperty, asc, localSeq);
	}
	@Override
	public List<HistoricoAcomodacaoJnVO> obterHistoricoAcomodacaoJn(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short localSeq) {
		return mptLocalAtendimentoJnDAO.obterHistoricoAcomodacaoJn(firstResult, maxResults, orderProperty, asc, localSeq);
	}
	@Override
	public Long obterHistoricoSalaJnCount(Short salaSeq) {
		return mptDalasJnDAO.obterHistoricoSalaJnCount(salaSeq);
	}
	@Override
	public Long obterHistoricoAcomodacaoJnCount(Short localSeq) {
		return mptLocalAtendimentoJnDAO.obterHistoricoAcomodacaoJnCount(localSeq);
	}
	@Override
	public MptLocalAtendimento obterMptLocalAtendimentoPorSeq(Short localSeq) {
		return mptLocalAtendimentoDAO.obterPorChavePrimaria(localSeq);
	}
	
	@Override
	public List<MptTipoSessao> listarMptTiposSessao() {
		return this.mptTipoSessaoDAO.listarMptTiposSessao();
	}
	
	@Override
	public void inserirProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, Short diasTratamento) throws ApplicationBusinessException{
		this.mptProtocoloSessaoRN.inserirMptProtocoloSessao(tipoSessao, descricao, qtdCiclo, versao, diasTratamento); 
	}
		
	@Override
	public MptVersaoProtocoloSessao inserirCopiaProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException{
		return this.mptProtocoloSessaoRN.inserirCopiaProtocolo(tipoSessao, descricao, qtdCiclo, versao, listaProtocoloMedicamentosVO, listaProtocoloSolucoes, listaProtocoloCuidados, diasTratamento); 
	}
		
	@Override
	public MptProtocoloSessao verificaProtocoloPorDescricao(String descrProtocolo) {
		return this.mptProtocoloSessaoDAO.verificaProtocoloPorDescricao(descrProtocolo);
	}

	@Override
	public List<ProtocoloVO> pesquisarProtocolosAtivos(ProtocoloVO filtro,
			Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc) {
		return procedimentoTerapeuticoRN.pesquisarProtocolosAtivos(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long contarPesquisarProtocolosAtivos(ProtocoloVO filtro) {
		return procedimentoTerapeuticoRN.contarPesquisarProtocolosAtivos(filtro);
	}
	
	@Override
	public List<ProtocoloVO> pesquisarProtocolosInativos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		return mptVersaoProtocoloSessaoDAO.pesquisarProtocolosInativos(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long contarPesquisarProtocolosInativos(ProtocoloVO filtro) {
		return mptVersaoProtocoloSessaoDAO.contarPesquisarProtocolosInativos(filtro);
	}
	
	@Override
	public Long obterMedicamentosAtivosCount(String objPesquisa) {
		return afaMedicamentoDAO.obterMedicamentosAtivosCount(objPesquisa);
	}
		
	@Override
	public List<AfaMedicamento> obterMedicamentosAtivos(String objPesquisa) {
		return afaMedicamentoDAO.obterMedicamentosAtivos(objPesquisa);
	}
	
	@Override
	public List<MptFavoritosServidor> obterFavoritosTipoSessao(Integer matriculaServidor, Short vinCodigoServidor) {
		return mptFavoritosServidorDAO.obterFavoritosTipoSessao(matriculaServidor, vinCodigoServidor);
	}
	
	@Override
	public MptTipoSessao obterTipoSessaoPorId(Short id) {
		return mptTipoSessaoDAO.obterPorChavePrimaria(id);
	}

	@Override	
	public List<DominioSituacaoProtocolo> verificarSituacaoProtocolo(Integer seqProtocolo){
		return this.mptProtocoloSessaoRN.verificarSituacaoProtocolo(seqProtocolo); 
	}
	
	@Override
	public MptVersaoProtocoloSessao obterSituacaoProtocolo(Integer seqProtocolo){
		return this.mptVersaoProtocoloSessaoDAO.verificaSituacaoProtocoloPorSeq(seqProtocolo);
	}
	
	@Override
	public void atualizarProtocolo(MptTipoSessao tipoSessao, String descricao, Integer qtdCiclo, Integer versao, Integer seqProtocolo, DominioSituacaoProtocolo situacaoProtocolo, Integer seqVersaoProtocoloSessao) throws ApplicationBusinessException{
		this.mptProtocoloSessaoRN.atualizarProtocolo(tipoSessao, descricao, qtdCiclo, versao, seqProtocolo, situacaoProtocolo, seqVersaoProtocoloSessao); 
	}
	
	@Override
	public MptProtocoloSessao obterMptProtocoloSessaoPorSeq(Integer seqProtocolo){
		return this.mptProtocoloSessaoRN.obterMptProtocoloSessaoPorSeq(seqProtocolo);
	}
	
	@Override
	public ProtocoloSessaoVO obterItemVersaoProtocolo(Integer seqVersaoProtocolo) {
		return this.mptProtocoloSessaoRN.obterItemVersaoProtocolo(seqVersaoProtocolo);
	}
	
	@Override
	public List<MptVersaoProtocoloSessao> pesquisarVersoesProtocolo(Integer seqProtocoloSessao) {
		return mptVersaoProtocoloSessaoDAO.pesquisarVersoesProtocolo(seqProtocoloSessao);
	}
	
	@Override
	public void excluirVersaoProtocolo(ProtocoloVO itemExclusao) {
		procedimentoTerapeuticoRN.excluirVersaoProtocolo(itemExclusao);
	}

	@Override
	public List<MptTipoSessao> pesquisarTipoSessoesAtivas() {
		return mptTipoSessaoDAO.pesquisarTipoSessoesAtivas();
	}

	@Override
	public List<MptCaracteristica> recuperarListaDeCaracteristicas(MptTipoSessao tipoSessaoFiltro, String descricaoFiltro, DominioSituacao situacao) {
		return procedimentoTerapeuticoRN.recuperarListaDeCaracteristicas(tipoSessaoFiltro, descricaoFiltro, situacao);
	}

	@Override
	public List<MptCaracteristicaJn> pesquisarAlteracoesCaracteristica(Short carSeq) {
		return mptCaracteristicaJnDAO.pesquisarAlteracoesCaracteristica(carSeq);
	}

	@Override
	public void salvarCaracteristica(MptCaracteristica caracteristica, String usuarioLogado) throws ApplicationBusinessException {
		procedimentoTerapeuticoRN.salvarCaracteristica(caracteristica, usuarioLogado);
	}
	
	@Override
	public void salvarTipoIntercorrente(MptTipoIntercorrencia tipoIntercorrente, boolean situacao, boolean emEdicao) throws ApplicationBusinessException{
		this.mptTipoIntercorrenciaRN.salvarTipoIntercorrente(tipoIntercorrente, situacao, emEdicao);
	}
	
	@Override
	public List<MptTipoIntercorrenciaJnVO> obterHistoricoTiposIntercorrentes(Integer seq){
		return this.mptTipoIntercorrenciaJnDAO.carregarRegistrosTiposIntercorrencia(seq);
	}
	
	@Override
	public Long listarTiposIntercorrentesCount(String descricao, DominioSituacao situacao){
		return this.mptTipoIntercorrenciaDAO.listarTiposIntercorrentesCount(descricao, situacao);
	}
	
	@Override
	public List<MptTipoIntercorrencia> listarTiposIntercorrentes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			String descricao, DominioSituacao situacao){
		return this.mptTipoIntercorrenciaDAO.listarTiposIntercorrentes(firstResult, maxResults, orderProperty, asc, descricao, situacao);
	}
	
	@Override
	public boolean verificarDescricaoExistente(String descricao){
		return this.mptTipoIntercorrenciaDAO.verificarDescricaoExistente(descricao);
	}
	
	@Override
	public boolean verificarVinculoPorTipoIntercorrente(Short seq){
		return this.mptTipoSessaoTipoIntercorDAO.verificarVinculoPorTipoIntercorrente(seq);
	}
	@Override
	public List<MptTipoSessao> listarTiposSessaoCombo() {		 
		return mptTipoSessaoDAO.   listarTipoSessaoCombo();
	}

	@Override
	public String pesquisarProtocoloGrid(Integer cloSeq) {		 
		return mptProtocoloCicloRN.pesquisarProtocoloGrid(cloSeq);
	}

	@Override
	public String nomeResponsavel(String nome1, String nome2) {		 
		return mptProtocoloCicloRN.nomeResponsavel(nome1,nome2);
	}

	@Override
	public Long pesquisarAgendmento(Integer lote,Integer consulta) {		 
		return mptPrescricaoPacienteDAO.pesquisarAgendamento(lote,consulta);
	}

	@Override
	public List<PacientesTratamentoSessaoVO> pesquisarPacientesTratamentoSessao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MptTipoSessao tipoSessao, Date periodoInicial,
			Date periodoFinal, Boolean checkAberto, Boolean checkFechado,
			Boolean checkPrimeiro, Boolean checkSessao, Integer codigo,
			Integer tipoFiltroPersonalizado, Integer parametro) {
		 
		return mptPrescricaoPacienteDAO.unionListas(
				firstResult, maxResult, orderProperty, asc, tipoSessao, 
				periodoInicial, periodoFinal, checkAberto, checkFechado,
				checkPrimeiro, checkSessao, codigo, tipoFiltroPersonalizado, parametro);
	}

	@Override
	public MptFavoritoServidor pesquisarTipoSessaoUsuario(Integer matricula, Short vinCodigo) {
		return mptFavoritoServidorDAO.pesquisarTipoSessaoUsuario(matricula, vinCodigo);		 	
	}

	@Override
	public MptTipoSessao pesquisarTipoSessaaoFavorito(MptTipoSessao tipoSessao) {		 
		return mptTipoSessaoDAO.obterOriginal(tipoSessao);
	}

	@Override
	public Long pesquisarPacientesTratamentoSessaoCount(
			MptTipoSessao tipoSessao, Date periodoInicial, Date periodoFinal,
			Boolean checkAberto, Boolean checkFechado, Boolean checkPrimeiro,
			Boolean checkSessao, Integer codigoPaciente,
			Integer tipoFiltroPersonalizado, Integer parametro) {
		 
		return mptPrescricaoPacienteDAO.unionListasCount(tipoSessao, periodoInicial, periodoFinal, checkAberto, checkFechado, checkPrimeiro, checkSessao, codigoPaciente, tipoFiltroPersonalizado, parametro);
	}
	
	@Override
	public MptCaracteristicaTipoSessao obterVinculoCaracteristicaTipoSessao(MptCaracteristica mptCaracteristica, MptTipoSessao mptTipoSessao){
		return mptCaracteristicaTipoSessaoDAO.obterVinculoCaracteristicaTipoSessao(mptCaracteristica, mptTipoSessao);
	}
	
	@Override
	public MptCaracteristicaTipoSessao obterCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao){
		return mptCaracteristicaTipoSessaoDAO.obterCaracteristicaTipoSessao(mptTipoSessao, mptCaracteristicaTipoSessao);
	}
	
	@Override
	public void adicionarMptCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristica mptCaracteristica) throws ApplicationBusinessException{
		mptCaracteristicaTipoSessaoON.adicionarMptCaracteristicaTipoSessao(mptTipoSessao, mptCaracteristica);
	}
	@Override
	public void excluirMptCaracteristicaTipoSessao(MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao) {
		mptCaracteristicaTipoSessaoON.excluirMptCaracteristicaTipoSessao(mptCaracteristicaTipoSessao);
	}

	@Override
	public void inserirMptCaracteristica(MptCaracteristica mptCaracteristica){
		mptCaracteristicaON.inserirMptCaracteristica(mptCaracteristica);
	}
	
	@Override
	public List<MptCaracteristica> obterListaCaracteristicaDescricao(String strPesquisa){
		return mptCaracteristicaDAO.obterListaCaracteristicaDescricao(strPesquisa);
	}
	
	@Override
	public Long listarCaracteristicaCount(String strPesquisa){
		return mptCaracteristicaDAO.listarCaracteristicaCount(strPesquisa);
	}
	
	@Override
	public MptCaracteristica obterMptCaracteristicaPorSeq(MptCaracteristica mptCaracteristica){
		return mptCaracteristicaDAO.obterMptCaracteristicaPorSeq(mptCaracteristica);
	}
	
	@Override
	public Long obterCaracteristicaDescricaoOuSiglaCount(String strPesquisa){
		return mptCaracteristicaDAO.obterCaracteristicaDescricaoOuSiglaCount(strPesquisa);
	}
	
	@Override
	public List<MptCaracteristicaTipoSessao> obterCaracteristicaTipoSessaoPorTpsSeq(MptTipoSessao mptTipoSessao){
		return mptCaracteristicaTipoSessaoDAO.obterCaracteristicaTipoSessaoPorTpsSeq(mptTipoSessao);
	}
	
	@Override
	public  MptCaracteristicaTipoSessao obterCaracteristicaTipoSessaoPorCarSeq(MptCaracteristica mptCaracteristica){
		return  mptCaracteristicaTipoSessaoDAO.obterCaracteristicaTipoSessaoPorCarSeq(mptCaracteristica);
	}
	
	
	@Override
	public List<VincularIntercorrenciaTipoSessaoVO> carregarVinculosTipoIntercorreciaTipoSessao(MptTipoSessao tipoSessao, String descricao,
			Integer firstResult, Integer maxResults) {
		return mptTipoSessaoTipoIntercorDAO.carregarVinculosTipoIntercorreciaTipoSessao(tipoSessao, descricao, firstResult, maxResults);
	}

	@Override
	public Long carregarVinculosTipoIntercorreciaTipoSessaoCount(MptTipoSessao tipoSessao, String descricao) {
		return mptTipoSessaoTipoIntercorDAO.carregarVinculosTipoIntercorreciaTipoSessaoCount(tipoSessao, descricao);
	}

	@Override
	public List<MptTipoIntercorrencia> carregarTiposIntercorrencia(String pesquisa) {
		return mptTipoIntercorrenciaDAO.carregarTiposIntercorrencia(pesquisa);
	}

	@Override
	public Long carregarTiposIntercorrenciaCount(String pesquisa) {
		return mptTipoIntercorrenciaDAO.carregarTiposIntercorrenciaCount(pesquisa);
	}

	@Override
	public Long verificarExistenciaVinculoIntercorrenciaTipoSessao(
			Short seqTipoIntercorrencia, Short seqTipoSessao) {
		return mptTipoSessaoTipoIntercorDAO.verificarExistenciaVinculoIntercorrenciaTipoSessao(seqTipoIntercorrencia, seqTipoSessao);
	}
	
	@Override
	public boolean excluirVinculoIntercorrrenciaTipoSessao(Short seq){
		return this.mptTipoSessaoTipoIntercorRN.excluirVinculoIntercorrrenciaTipoSessao(seq);
	}

	@Override
	public Boolean verificarVinculo(Short seqTipoIntercorrencia,
			Short seqTipoSessao) {
		return mptTipoSessaoTipoIntercorRN.verificarVinculo(seqTipoIntercorrencia, seqTipoSessao);
	}

	@Override
	public boolean adicionarVinculoIntercorrrenciaTipoSessao(
			MptTipoIntercorrencia tipoIntercorrencia, MptTipoSessao tipoSessao)
			throws ApplicationBusinessException {
		return this.mptTipoSessaoTipoIntercorRN.adicionarVinculoIntercorrrenciaTipoSessao(tipoIntercorrencia, tipoSessao);
	}
	
	@Override
	public List<String> validarPersistenciaCaracteristica(MptCaracteristica caracteristica) {
		return procedimentoTerapeuticoRN.validarPersistenciaCaracteristica(caracteristica);
	}

	@Override
	public void validarInativacaoStatusCaracteristica(MptCaracteristica caracteristica, Boolean situacao)	throws ApplicationBusinessException {
		procedimentoTerapeuticoRN.validarInativacaoStatusCaracteristica(caracteristica, situacao);
	}
	
	@Override
	public List<HistoricoSessaoJnVO> obterHistoricoSessaoJn(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short sessaoSeq) {
		return mptTipoSessaoRN.obterHistoricoSessaoJn(firstResult, maxResults, orderProperty, asc, sessaoSeq);
}
	@Override
	public Long obterHistoricoSessaoJnCount(Short sessaoSeq) {
		return mptTipoSessaoJnDAO.obterHistoricoSessaoJnCount(sessaoSeq);
	}
	@Override
	public MptTipoSessao obterTipoSessaoOriginal(Short sessaoSeq) {
		return mptTipoSessaoRN.obterTipoSessaoComUnidadeFuncionalPreenchida(sessaoSeq);
	}
	@Override
	public MptSalas obterSalaPorSeqAtiva(Short seq) {
		return mptSalasDAO.obterSalaPorSeqAtiva(seq);
	}
	@Override
	public MptTipoSessao obterMptTipoSessaoPorSeqAtivo(Short seq) {
		return mptTipoSessaoDAO.obterMptTipoSessaoPorSeqAtivo(seq);
	}
	@Override
	public MptFavoritoServidor obterFavoritoPorServidor(RapServidores servidor) {
		return this.mptServidorFavoritoDAO.obterPorServidor(servidor);
	}
	@Override
	public List<MapaDisponibilidadeVO> consultarMapaDisponibilidade(Short tipoSessaoSeq, Short salaSeq, Date dataInicio, DominioTurno turno) 
			throws ParseException {
		return this.mapaDisponibilidadeRN.consultarMapaDisponibilidade(tipoSessaoSeq, salaSeq, dataInicio, turno);
	}
	@Override
	public List<MptTurnoTipoSessao> obterTurnosPorTipoSessaoOrdenado(Short tpsSeq) {
		return this.mptTurnoTipoSessaoDAO.obterTurnosPorTipoSessaoOrdenado(tpsSeq);
	}
	@Override
	public List<MpaProtocoloAssistencial> buscarProtocolo(Object parametro, Short codigoTipoSessao) {
		
		return protocoloAssistencialRN.buscarProtocolo(parametro, codigoTipoSessao);
	}
	@Override
	public Long buscarProtocoloCount(Object parametro, Short codigoSala) {
		
		return protocoloAssistencialRN.buscarProtocoloCount(parametro, codigoSala);
	}
	@Override
	public List<MptLocalAtendimento> buscarLocalAtendimento(Object parametro, Short codigoSala) {
		
		return mptLocalAtendimentoRN.buscarLocalAtendimento(parametro, codigoSala);
	}
	@Override
	public Long buscarLocalAtendimentoCount(Object parametro, Short codigoSala) {
		
		return mptLocalAtendimentoRN.buscarLocalAtendimentoCount(parametro, codigoSala);
	}
	@Override
	public List<MptTipoSessao> buscarTipoSessao() {
		
		return mptTipoSessaoDAO.buscarTipoSessao();
	}
	@Override
	public List<MptSalas> buscarSala(Short codigoTipoSessao) {
		
		return mptSalasRN.buscarSala(codigoTipoSessao);
	}
	@Override
	public MptFavoritoServidor pesquisarFavoritosServidor(RapServidores usuarioLogado){
		
		return mptFavoritoServidorDAO.buscarFavoritosServidor(usuarioLogado);
	}
	@Override
	public MptTurnoTipoSessao obterHorariosTurnos(Short tipoSessaoSeq, DominioTurno turno) {
		
		return mptHorarioSessaoRN.obterHorariosTurnos(tipoSessaoSeq, turno);
	}
	@Override
	public List<ListaPacienteAgendadoVO> pesquisarListaPacientes(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		return mptHorarioSessaoRN.pesquisarListaPacientes(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}
	@Override
	@BypassInactiveModule			
	public Long pesquisarListaPacientesCount(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		return mptHorarioSessaoRN.pesquisarListaPacientesCount(dataInicio,horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}
	@Override
	public List<ListaPacienteAguardandoAtendimentoVO> pesquisarListaPacientesAguardandoAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		return mptHorarioSessaoRN.pesquisarListaPacientesAguardandoAtendimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}	
	@Override
	public String validarCampos(Date dataInicio, Short tipoSessao) throws BaseListException {
		return mptHorarioSessaoRN.validarCampos(dataInicio, tipoSessao);
	}
	@Override
	public void validarCampoTipoSessao(Short tipoSessao) throws ApplicationBusinessException {
		mptHorarioSessaoRN.validarCampoTipoSessao(tipoSessao);
	}	
	@Override
	public void validarCampoSala(Short sala) throws ApplicationBusinessException {
		mptHorarioSessaoRN.validarCampoSala(sala);
	}
	@Override
	public String validarListagem(List<ListaPacienteAgendadoVO> listagem) throws ApplicationBusinessException {
		return mptHorarioSessaoRN.validarListagem(listagem);
	}
	@Override
	public String validarListagemAba3(List<ListaPacienteAguardandoAtendimentoVO> listagem) throws ApplicationBusinessException {
		return mptHorarioSessaoRN.validarListagemAba3(listagem);
	}
	@Override
	public void chegouPaciente(ListaPacienteAgendadoVO paciente) {
		mptSessaoRN.chegouPaciente(paciente);
	}	
	@Override
	public void emAtendimento(ListaPacienteAguardandoAtendimentoVO paciente) {
		mptSessaoRN.emAtendimento(paciente);
	}	
	@Override
	public void voltarAcolhimento(ListaPacienteAguardandoAtendimentoVO parametroSelecionado) {
		mptSessaoRN.voltarAcolhimento(parametroSelecionado);
	}
	
	@Override
	public List<ListaPacienteEmAtendimentoVO> pesquisarListaPacientesEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		return mptHorarioSessaoRN.pesquisarListaPacientesEmAtendimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}
	@Override
	public String validarListagemAba4(List<ListaPacienteEmAtendimentoVO> listagem) throws ApplicationBusinessException {
		return mptHorarioSessaoRN.validarListagemAba4(listagem);
	}	
		
	@Override
	public void concluirAtendimento(Integer seqSessao){
		mptSessaoRN.concluirAtendimento(seqSessao);
	}	
	@Override
	public void voltarAguardandoAte(ListaPacienteEmAtendimentoVO parametroSelecionado) {
		mptSessaoRN.voltarAguardandoAte(parametroSelecionado);
	}
	@Override
	public void medicamentoDomiciliar(ListaPacienteEmAtendimentoVO medDomiciliar) {
		mptSessaoRN.medicamentoDomiciliar(medDomiciliar);
	}
	@Override
	public BigDecimal tempoManipulacao() {
		return mptHorarioSessaoRN.tempoManipulacao();
	}
	/**
	 * #44227 - Bloqueio de Salas e Acomodações 
	 */
	@Override
	public Date pesquisarHoraInicio(Integer seqSessao) {
		return mptHorarioSessaoRN.pesquisarHoraInicio(seqSessao);
	}
	@Override
	public void validarHorario(MptSessao MptSessao) throws ApplicationBusinessException {
		mptHorarioSessaoRN.validarHorario(MptSessao);
	}
	@Override
	public void inserir(MptSessao mptSessao, Date dataInicio) {
		mptSessaoRN.inserir(mptSessao, dataInicio);
	}
	@Override
	public List<MptBloqueio> pesquisarIndisponibilidadeSalaAcomodacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MptBloqueio filtro){
		return getMptBloqueioDAO().listarSalaEAcomodacaoIndisponiveis(firstResult, maxResult, orderProperty,
				asc, filtro);
	}
	@Override
	public List<MptLocalAtendimento> buscarLocalAtendimento(Short codigoSala) {
		return getMptLocalAtendimentoDAO().pesquisarAcomodacaoPorSala(codigoSala);
	}
	@Override
	public Long pesquisarIndisponibilidadeSalaAcomodacaoCount(MptBloqueio filtro) {
		return getMptBloqueioDAO().listarSalaEAcomodacaoIndisponiveisCount(filtro);
	}
	@Override
	public void persistirMptBloqueio(MptBloqueio bloqueio)
			throws ApplicationBusinessException, BaseException {
		mptBloqueioDAO.desatachar(bloqueio);
		mptBloqueioDAO.persistir(bloqueio);
		this.flush();
	}
	public void atualizarMptBloqueio(MptBloqueio bloqueio){
		mptBloqueioDAO.atualizar(bloqueio);
	}
	@Override
	public List<MptJustificativa> buscarJustificativa(String filtro) {
		return getMptJustificativaDAO().pesquisarJustificativa(filtro);
	}
	@Override
	public Long buscarJustificativaCount(String filtro) {
		return getMptJustificativaDAO().pesquisarJustificativaCount(filtro);
	}
	public MptLocalAtendimentoDAO getMptLocalAtendimentoDAO(){
		return mptLocalAtendimentoDAO;
	}
	public MptBloqueioDAO getMptBloqueioDAO() {
		return mptBloqueioDAO;
	}
	public MptJustificativaDAO getMptJustificativaDAO() {
		return mptJustificativaDAO;
	}
	public void setMptJustificativaDAO(MptJustificativaDAO mptJustificativaDAO) {
		this.mptJustificativaDAO = mptJustificativaDAO;
	}
	@Override
	public List<MptTipoSessao> obterTipoSessaoDescricao() {
		return mptTipoSessaoDAO.obterTipoSessaoDescricao();
	}
	@Override
	public List<MptSalas> obterSalaPorTipoSessao(Short seqTipoSessao) {
		return mptSalasDAO.obterSalaPorTipoSessao(seqTipoSessao);
	}
	@Override
	public ManterFavoritosUsuarioVO obterFavoritoPorPesCodigo(RapServidores servidor) {
		return mptFavoritoServidorDAO.obterFavoritoPorPesCodigo(servidor);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroFavoritos','executar')}")
	public void removerMptFavoritoServidor(Integer favoritoSeq) throws BaseException {
		mptFavoritoServidorRN.removerMptFavoritoServidor(favoritoSeq);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroFavoritos','executar')}")
	public ManterFavoritosUsuarioVO adicionarFavorito (MptSalas sala,  MptTipoSessao tipoSessao, ManterFavoritosUsuarioVO favorito) throws BaseListException  {
		return mptFavoritoServidorRN.adicionarFavorito(sala, tipoSessao, favorito);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroFavoritos','executar')}")
	public void  persistirMptFavoritoServidor(ManterFavoritosUsuarioVO favoritoVO, RapServidores servidor){
		mptFavoritoServidorRN.persistirMptFavoritoServidor(favoritoVO, servidor);
	}
	public void removerMptBloqueio(MptBloqueio item){
		mptBloqueioDAO.removerPorId(item.getSeq());
	}
	@Override
	public MptTipoSessao obterTipoSessaoPorChavePrimaria(Short seq) {
		return this.mptTipoSessaoDAO.obterPorChavePrimaria(seq);
	}
	@Override
	public List<MptJustificativa> pesquisarMotivoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		return this.mptJustificativaDAO.pesquisarMotivoManutencaoAgendamentoSessaoTerapeutica(filtro);
	}
	@Override
	public Long pesquisarMotivoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro) {
		return this.mptJustificativaDAO.pesquisarMotivoManutencaoAgendamentoSessaoTerapeuticaCount(filtro);
	}
	@Override
	public List<AlterarHorariosVO> pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Integer codigoPaciente) throws ApplicationBusinessException {
		return this.manutencaoAgendamentoSessaoTerapeuticaRN.pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(codigoPaciente);
	}
	@Override
	public List<DiasAgendadosVO> pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(Short agsSeq) {
		return this.mptHorarioSessaoDAO.pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(agsSeq);
	}
	@Override
	public List<MptTipoSessao> pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeutica(String filtro) {
		return this.mptTipoSessaoDAO.pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeutica(filtro);
	}
	@Override
	public Long pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeuticaCount(String filtro) {
		return this.mptTipoSessaoDAO.pesquisarTipoSessaoManutencaoAgendamentoSessaoTerapeuticaCount(filtro);
	}
	@Override
	public void removerCicloManutencaoAgendamentoSessaoTerapeutica(Short seqAgendamento, Integer seqSessao, Short seqMotivo, String justificativa) {
		this.manutencaoAgendamentoSessaoTerapeuticaRN.removerCicloManutencaoAgendamentoSessaoTerapeutica(seqAgendamento, seqSessao, seqMotivo, justificativa);
	}
	@Override
	public void removerDiaManutencaoAgendamentoSessaoTerapeutica(Short seqHorario, Short seqAgendamento, Integer seqSessao, Short seqMotivo, String justificativa) {
		this.manutencaoAgendamentoSessaoTerapeuticaRN.removerDiaManutencaoAgendamentoSessaoTerapeutica(seqHorario, seqAgendamento, seqSessao, seqMotivo, justificativa);
	}
	@Override
	public MptSalas obterMptSalaPorChavePrimaria(Short seq) {
		return mptSalasDAO.obterPorChavePrimaria(seq);
	}
	@Override
	public List<MptTipoSessao> pesquisarMptTipoSessaoAtivos() {
		return this.mptTipoSessaoDAO.pesquisarMptTipoSessaoAtivos();
	}
	@Override
	public TransferirDiaVO obterLabelRestricaoDatas(List<DiasAgendadosVO> dias) throws ApplicationBusinessException {
		return this.manutencaoAgendamentoSessaoTerapeuticaON.obterLabelRestricaoDatas(dias);
	}
	@Override
	public boolean ativarTransferirDia(List<DiasAgendadosVO> dias) {
		return this.manutencaoAgendamentoSessaoTerapeuticaON.ativarTransferirDia(dias);
	}
	@Override
	public void transferirDia(TransferirDiaVO transferencia, AlterarHorariosVO ciclo, DiasAgendadosVO dia) throws ApplicationBusinessException {
		this.manutencaoAgendamentoSessaoTerapeuticaRN.transferirDia(transferencia, ciclo, dia);
	}
	//42292
	@Override
	public List<ImpressaoTicketAgendamentoVO> obterListaRegistroHorariosSessao(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta){
		return mptHorarioSessaoDAO.obterHorariosSessaoPorPacientePrescricao(codPaciente, codCiclo, numeroConsulta);
	}
	
	@Override
	public List<ReservasVO> obterHorariosSessaoPorPacientePrescricaoReservas(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta){
		return mptHorarioSessaoDAO.obterHorariosSessaoPorPacientePrescricaoReservas(codPaciente, codCiclo, numeroConsulta);
	}
	@Override
	public List<ReservasVO> pesquisarConsultasReservas(Integer codigoPaciente, Integer consulta) {
		return mptAgendamentoSessaoDAO.pesquisarConsultasReservas(codigoPaciente, consulta);
	}
	@Override
	public List<ReservasVO> pesquisarConsultarDiasReservas(Short codigoAgsSessao){
		return mptHorarioSessaoDAO.pesquisarConsultarDiasReservas(codigoAgsSessao);
	}
	@Override
	public List<ImpressaoTicketAgendamentoVO> obterListaCiclo(Integer codPaciente, List<Integer> codCiclo, Integer numeroConsulta){
		return imprimirTicketAgendamentoRN.obterListaCiclo(codPaciente, codCiclo, numeroConsulta);
	}
	@Override
	public List<ReservasVO> obterListaCicloReservas(Integer codPaciente, List<Integer> listCicloSeq){
		return imprimirTicketAgendamentoRN.obterListaCicloReservas(codPaciente, listCicloSeq);
	}
	@Override
	public String obterProtocolos(List<Integer> listCloSeq){
		return imprimirTicketAgendamentoRN.obterProtocolos(listCloSeq);
	}
	@Override
	public String obterTextoTicket(AipPacientes paciente, List<Integer> codCiclo) throws ApplicationBusinessException{
		return imprimirTicketAgendamentoRN.obterTextoTicket(paciente, codCiclo);
	}
	@Override
	public String obterTextoTicketReservadas(AipPacientes paciente, List<Integer> listCicloSeq, Short seqAgendamento) throws ApplicationBusinessException{
		return imprimirTicketAgendamentoRN.obterTextoTicketReservadas(paciente, listCicloSeq, seqAgendamento);
	}
	@Override
	public MptFavoritoServidor obterServidorSelecionadoPorMatriculaVinculo(
			Integer matricula, Short vinculo) {
		return mptFavoritoServidorDAO.obterServidorSelecionadoPorMatriculaVinculo(matricula, vinculo);
	}
	@Override
	public List<MptSalas> obterListaSalasPorTpsSeq(Short tpsSeq) {
		return mptSalasDAO.obterListaSalasPorTpsSeq(tpsSeq);
	}
	@Override
	public List<MptTipoSessao> obterListaTipoSessaoPorIndSituacaoAtiva() {
		return mptTipoSessaoDAO.obterListaTipoSessaoPorIndSituacaoAtiva();
	}
	@Override
	public List<DiaPrescricaoVO> obterDiaPrescricaoVO(Integer lote, Integer cloSeq) {
		return mptSessaoDAO.obterDiaPrescricaoVO(lote, cloSeq);
	}
	@Override
	public List<ProtocoloPrescricaoVO> obterProtocoloPrescricaoVOPorCloSeq(
			Integer cloSeq) {
		return mptProtocoloCicloDAO.obterProtocoloPrescricaoVOPorCloSeq(cloSeq);
	}
	@Override
	public List<MptJustificativa> obterListaJustificativaSB(String descricao) {
		return mptJustificativaDAO.obterListaJustificativaSB(descricao);
	}
	@Override
	public Long obterListaJustificativaSBCount(String descricao) {

		return mptJustificativaDAO.obterListaJustificativaSBCount(descricao);
	}
	@Override
	public List<PrescricaoPacientePTVO> obterListaPrescricaoPacientePTVO(Integer pacCodigo) {
		return mptSessaoDAO.obterListaPrescricaoPacientePTVO(pacCodigo);
	}
	@Override
	public void validarCampoObrigatorioAgendamentoSessaoExtra(MptTipoSessao tipoSessao, Date dataInicio, DiaPrescricaoVO diaPrescricaoVOSelecionado) throws ApplicationBusinessException {
		procedimentoTerapeuticoON.validarCampoObrigatorioAgendamentoSessaoExtra(tipoSessao, dataInicio, diaPrescricaoVOSelecionado);
	}
	@Override
	public void persistirMptAgendamentoSessao(MptAgendamentoSessao mptAgendamentoSessao) {
		mptAgendamentoSessaoDAO.persistir(mptAgendamentoSessao);
	}
	@Override
	public void persistirMptHorarioSessao(MptHorarioSessao horarioSessao) {
		mptHorarioSessaoDAO.persistir(horarioSessao);
	}
	@Override
	public void persistirMptSessaoExtra(MptSessaoExtra mptSessaoExtra) {
		MptSessaoExtraDAO.persistir(mptSessaoExtra);
	}
	@Override
	public MptSessao obterMptSessaoPorChavePrimaria(Integer seq) {
		return mptSessaoDAO.obterPorChavePrimaria(seq);
	}
	@Override
	public Date relacionarDiaSelecionado(Date horaInicio, DiaPrescricaoVO diaPrescricaoVO) throws ApplicationBusinessException {
		return procedimentoTerapeuticoON.relacionarDiaSelecionado(horaInicio, diaPrescricaoVO);
	}
	@Override
	public void persistirExtratoSessao(MptExtratoSessao mptExtratoSessao) {
		MptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}

	@Override
	public boolean exibirColunaApac(Short tipoSessaoSeq){
		return procedimentoTerapeuticoON.exibirColunaApac(tipoSessaoSeq);
	}
	
	@Override
	public List<AgendamentosPacienteVO> obterAgendamentosPaciente(Integer pacCodigo){
		return mptHorarioSessaoRN.obterAgendamentosPaciente(pacCodigo);
	}
	
	@Override
	public List<ExtratoSessaoVO>pesquisarExtratoSessao(Integer pacCodigo) {
		return MptExtratoSessaoDAO.pesquisarListaExtratoSessao(pacCodigo);
	}

	public AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	public void setAipPacientesDAO(AipPacientesDAO aipPacientesDAO) {
		this.aipPacientesDAO = aipPacientesDAO;
	}
	//#41736
	@Override
	public void registrarControleFrequenciaRelatorio(String dataAgendado, Integer codPaciente, Integer seqSessao, Long numeroApac, Byte capSeqApac, DominioControleFrequenciaSituacao dominioControleFrequencia, Date dataReferencia, RapServidores servidorLogado) {
		mptHorarioSessaoRN.registrarControleFrequenciaRelatorio(dataAgendado,codPaciente, seqSessao, numeroApac, capSeqApac, dominioControleFrequencia, dataReferencia, servidorLogado);
	}

	public AipPacientes dadosPacienteRelatorio(Integer prontuario){
		return aipPacientesDAO.pesquisarPacientePorProntuario(prontuario);
	}
	
	public ConsultaDadosAPACVO dadosPacienteAPAC(Date data, Integer horSessaoSeq){
		return mptHorarioSessaoDAO.pesquisarAPAC(data, horSessaoSeq);
	}
	
	@Override
	public List<ProcedimentosAPACVO>pesquisarprocedimentoApac(Long numeroApac) {
		return aacAtendimentoApacsDAO.obterProcedimentosApac(numeroApac);
	}

	@Override
	public List<ListaEsperaRetirarPacienteVO> pesquisarListaEsperaPacientes(AghParametros aghParametros, MptTipoSessao tipoSessao, Date dataPrescricao, Date dataSugerida,	AghEspecialidades especialidade, AipPacientes paciente) {		 
		return mptTipoSessaoDAO.pesquisarListaEsperaPacientes(aghParametros, tipoSessao, dataPrescricao, dataSugerida,	especialidade, paciente);
	}

	@Override
	public String buscarNomeResponsavel(String nome1, String nome2) {
		return mptTipoSessaoRN.buscarNomeResponsavel(nome1,nome2);
	}

	@Override
	public String buscarProtocolos(Integer cloSeq) {
		return mptProtocoloCicloRN.pesquisarProtocoloGrid(cloSeq);
	}

	@Override
	public List<CadIntervaloTempoVO> consultarDiasAgendamento(Integer lote) {		 
		return mptTipoSessaoDAO.consultarDiasAgendamento(lote);
	}

	@Override
	public AipPacientes obterPacientePorPacCodigo(Integer pacCodigo) {		 
		return aipPacientesDAO.pesquisarPacientePorCodigo(pacCodigo);
	}

	@Override
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, String horaInicio, String horaFim, Short tpsSeq, List<Short> sequenciaisSala) {
		return mptHorarioSessaoDAO.consultarTotalOcupacaoPorDiaPeriodoTipoSessao(dataInicio, dataFim, horaInicio, horaFim, tpsSeq, sequenciaisSala);
	}
	@Override
	public List<MptSalas> obterListaSalasPorTipoSessao(Short tpsSeq) {
		return mptSalasDAO.obterListaSalasPorTipoSessao(tpsSeq);
	}
	@Override
	public MptTurnoTipoSessao obterTurnoTipoSessaoPorTurnoTpsSeq(DominioTurno turno, Short tpsSeq) {
		return mptTurnoTipoSessaoDAO.obterTurnoTipoSessaoPorTurnoTpsSeq(turno, tpsSeq);
	}
	@Override
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoSalaPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim) {
		return mptHorarioSessaoDAO.consultarTotalOcupacaoSalaPorDiaPeriodoTipoSessao(dataInicio, dataFim, sala, horaInicio, horaFim);
	}
	@Override
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoPoltronaPorDiaPeriodoSala(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim, List<Short> sequenciaisSala) {
		return mptHorarioSessaoDAO.consultarTotalOcupacaoPoltronaPorDiaPeriodoSala(dataInicio, dataFim, sala, horaInicio, horaFim, sequenciaisSala);
	}
	@Override
	public List<MptFavoritoServidor> obterFavoritos(Integer matriculaServidor, Short vinCodigoServidor) {
		return mptFavoritoServidorDAO.obterFavoritos(matriculaServidor, vinCodigoServidor);
	}
	
	@Override
	public List<PacienteAcolhimentoVO> obterPacientesParaAcolhimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao,
			Short sala, MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao,MpaProtocoloAssistencial mpaProtocoloAssistencial) {

		return mptHorarioSessaoRN.obterPacientesParaAcolhimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}

	@Override
	public String validarListagemAcolhimento(List<PacienteAcolhimentoVO> listagem) throws ApplicationBusinessException {
		return mptHorarioSessaoRN.validarListagemAcolhimento(listagem);
	}

	@Override
	public void concluirAcolhimento(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException{
		mptSessaoRN.concluirAcolhimento(paciente);
	}

	@Override
	public void marcarMedicamentoDomiciliar(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException {
		mptSessaoRN.marcarMedicamentoDomiciliar(paciente);
	}

	@Override
	public List<MptJustificativa> obterJustificativaParaSuspensao(Short tipoSessao) {
		return mptJustificativaDAO.obterJustificativaParaSuspensao(tipoSessao);
	}

	@Override
	public void confirmarSuspensaoSessao(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException {
		mptSessaoRN.confirmarSuspensaoSessao(paciente);
	}

	@Override
	public void voltarParaAgendado(PacienteAcolhimentoVO paciente) throws ApplicationBusinessException {
		mptSessaoRN.voltarParaAgendado(paciente);
	}

	@Override
	public void registrarImpressaoPulseira(PacienteAcolhimentoVO paciente) {
		mptSessaoRN.registrarImpressaoPulseira(paciente);
	}
	
	@Override
	public MptJustificativa obterMptJustificativaVO(JustificativaVO mptJustificativa){
		return this.mptJustificativaDAO.obterMptJustificativaVO(mptJustificativa);
	}
	
	@Override
	public MptJustificativa obterMptJustificativa(MptJustificativa mptJustificativa){
		return this.mptJustificativaDAO.obterMptJustificativa(mptJustificativa);
	}
	
	@Override
	public void excluirJustificativa(MptJustificativa mptJustificativa)throws ApplicationBusinessException{
		this.manterTipoJustificativaON.excluirJustificativa(mptJustificativa);
	}

	@Override
	public void editarJustificativa(MptJustificativa mptJustificativa) throws ApplicationBusinessException{
		this.manterTipoJustificativaON.editarJustificativa(mptJustificativa);
	}
	
	@Override
	public void adicionarJustificativa(MptJustificativa mptJustificativa) throws ApplicationBusinessException{
		this.manterTipoJustificativaON.adicionarJustificativa(mptJustificativa);
	}
	

	
	@Override
	public Long listarMptTipoJustificativaCount(String param) {
		return this.mptTipoJustificativaDAO.listarMptTipoJustificativaCount(param);
	}
	
	@Override
	public List<MptTipoJustificativa> listarMptTipoJustificativa(String strPesquisa) {
		return this.mptTipoJustificativaDAO.listarMptTipoJustificativa(strPesquisa);
	}
	
	@Override
	public List<JustificativaVO> obterJustificativas(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, MptJustificativa mptJustificativa){
		return this.mptJustificativaDAO.obterJustificativas(firstResult, maxResult, 
				orderProperty, asc, mptJustificativa);
	}
	
	@Override
	public Long listarMptJustificativaCount(MptJustificativa mptJustificativa){
		return this.mptJustificativaDAO.listarMptJustificativaCount(mptJustificativa);
	}

	@Override
	public void registraControleFrequencia(MptControleFrequencia mptControleFrequencia) {
		mptControleFrequenciaRN.inserirControleFrequencia(mptControleFrequencia);
		
	}
	
	@Override
	public List<String> obterSiglaCaracteristicaPorTpsSeq(Short tpsSeq) {
		return mptCaracteristicaDAO.obterSiglaCaracteristicaPorTpsSeq(tpsSeq);
	}

	@Override
	public boolean exibirColuna(List<String> listaSiglas, String sigla) {
		return procedimentoTerapeuticoON.exibirColuna(listaSiglas, sigla);
	}
	
	@Override
	public List<AipPacientes> obtemPacientePorCodigo(Integer codigoPaciente) {
		return mptControleFrequenciaRN.consultaPacientePorCodigo(codigoPaciente);
	}

	@Override
	public String validarListagemConcluido(List<PacienteConcluidoVO> listagem) throws ApplicationBusinessException {
		return mptHorarioSessaoRN.validarListagemConcluido(listagem);
	}

	@Override
	public List<PacienteConcluidoVO> obterPacientesAtendimentoConcluido(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, MptLocalAtendimento acomodacao,
			DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {

		return mptHorarioSessaoRN.obterPacientesAtendimentoConcluido(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial);
	}

	@Override
	public void voltarParaAtendimento(PacienteConcluidoVO paciente) throws ApplicationBusinessException {
		mptSessaoRN.voltarParaAtendimento(paciente);
	}
	
	@Override
	public void marcarMedicamentoDomiciliar(PacienteConcluidoVO paciente) throws ApplicationBusinessException {
		mptSessaoRN.marcarMedicamentoDomiciliar(paciente);
	}
	
	@Override
	public List<MptTipoIntercorrenciaVO> obterIntercorrencias(){
		return mptTipoIntercorrenciaDAO.obterIntercorrencias();
	}
	
	@Override
	public List<RegistroIntercorrenciaVO> obterRegistroIntercorrenciaPorPaciente(Integer codigoPaciente){
		return procedimentoTerapeuticoRN.obterRegistroIntercorrenciaPorPaciente(codigoPaciente);
	}
	
	@Override
	public void gravarIntercorrencia(String descricao, Short tpiSeq, Integer sesSeq) throws ApplicationBusinessException{
		procedimentoTerapeuticoRN.gravarIntercorrencia(descricao, tpiSeq, sesSeq);
	}	
	
	@Override
	public List<RegistrarControlePacienteVO> carregarRegistrosIntercorrencia(Integer pacCodigo, Integer sesSeq) {
		return mptIntercorrenciaDAO.carregarRegistrosIntercorrencia(pacCodigo, sesSeq);
	}

	@Override
	public void liberarQuimioterapia(Integer codigo, Integer codigoAtendimento, Short seqTipoSessao) throws ApplicationBusinessException {
		mptSessaoRN.liberarQuimioterapia(codigo, codigoAtendimento, seqTipoSessao);
	}

	@Override
	public Boolean verificarPacienteAtrasadoSessao(Short seqHorario, Integer codPaciente) {
		return mptAgendamentoSessaoDAO.verificarPacienteAtrasadoSessao(seqHorario, codPaciente);
	}

	@Override
	public void registrarAusenciaPaciente(Integer seqSessao) {
		mptSessaoRN.registrarAusenciaPaciente(seqSessao);
	}

	@Override
	public void voltarStatusAgendado(Integer seqSessao) {
		mptSessaoRN.voltarStatusAgendado(seqSessao);
	}
	
	@Override
	public boolean verificarExisteSessao(Integer seqSessao) {
		return mptSessaoRN.verificarExisteSessao(seqSessao);
	}
	
	@Override
	public TaxaOcupacaoVO obterNomeSala(Short salSeq){
		return mptHorarioSessaoDAO.obterNomeSala(salSeq);
	}

	@Override
	public Long removerItensRN07(Integer lote, Integer cloSeq) {		 
		return this.mptPrescricaoPacienteDAO.removerItensRN07(lote, cloSeq);
	}
	
	@Override
	public List<Short> obterLocaisAtendimentoPorSala(Short salaSeq) {
		return mptLocalAtendimentoDAO.obterLocaisAtendimentoPorSala(salaSeq);
	}
	
	@Override
	public MptSalas obterSalaPorId(Short seqSala) {
		return mptSalasDAO.obterPorChavePrimaria(seqSala);
	}
	
	public Boolean existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Short seqAtd, Integer codigoPaciente){
		return mptAgendamentoSessaoDAO.existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(seqAtd, codigoPaciente);
	}
	
	public List<AlterarHorariosVO> pesquisarReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Integer codigoPaciente){
		return mptAgendamentoSessaoDAO.pesquisarReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(codigoPaciente);
	}

	
	@Override
	public List<MpmCuidadoUsual> listarCuidados(String strPesquisa) {
		return mpmCuidadoUsualDAO.listarCuidados(strPesquisa);		 
	}

	@Override
	public Number listarCuidadosCount(String strPesquisa) {
		return mpmCuidadoUsualDAO.listarCuidadosCount(strPesquisa);		 
	}

	@Override
	public List<MpmTipoFrequenciaAprazamento> listarAprazamentos(String strPesquisa) {		 
		return mpmTipoFrequenciaAprazamentoDAO.listarAprazamentos(strPesquisa);
	}

	@Override
	public Number listarAprazamentosCount(String strPesquisa) {		 
		return mpmTipoFrequenciaAprazamentoDAO.listarAprazamentosCount(strPesquisa);
	}

	@Override
	public void persistirProtocoloCuidados(MptProtocoloCuidados mptProtocoloCuidados) {
		mptProtocoloCuidadosDAO.persistir(mptProtocoloCuidados);				
	}

	@Override
	public List<MptProtocoloSessao> listarProtocolos(String strPesquisa, Integer seqProtocolo) {		 
		return mptProtocoloSessaoDAO.listarProtocolos(strPesquisa, seqProtocolo);
	}

	@Override
	public Long listarProtocolosCount(String strPesquisa, Integer seqProtocolo) {		 
		return mptProtocoloSessaoDAO.listarProtocolosCount(strPesquisa, seqProtocolo);
	}	
	
	@Override
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarListaTratamento(Integer seqVersaoProtocoloSessao) {
		return mptProtocoloMedicamentosDAO.pesquisarListaTratamento(seqVersaoProtocoloSessao);
	}
	@Override
	public void excluirTratamento(List<ProtocoloMedicamentoSolucaoCuidadoVO> lista, ProtocoloMedicamentoSolucaoCuidadoVO tratamento)throws ApplicationBusinessException{
		this.procedimentoTerapeuticoRN.excluirTratamento(lista, tratamento);
	}
	@Override
	public List<MedicamentosVO> pesquisarMedicamentos(String objPesquisa, Boolean padronizacao){
		return this.afaMedicamentoDAO.pesquisarMedicamento(objPesquisa, padronizacao);
	}
	@Override
	public Long pesquisarMedicamentosCount(String objPesquisa, Boolean padronizacao){
		return this.afaMedicamentoDAO.pesquisarMedicamentoCount(objPesquisa, padronizacao);
	}
	@Override
	public List<AfaViaAdministracao> pesquisarVia(String objPesquisa, Boolean todasVias, MedicamentosVO madMatCodigo){
		return this.afaViaAdministracaoDAO.pesquisaVia(objPesquisa, todasVias, madMatCodigo);
	}
	@Override
	public Long pesquisarViaCount(String objPesquisa, Boolean todasVias, MedicamentosVO madMatCodigo){
		return this.afaViaAdministracaoDAO.pesquisaViaCount(objPesquisa, todasVias, madMatCodigo);
	}
	@Override
	public List<MpmTipoFrequenciaAprazamento> pesquisarFrequenciaAprazamento(String objPesquisa){
		return this.mpmTipoFrequenciaAprazamentoDAO.pesquisaFrequenciaAprazamento(objPesquisa);
	}
	@Override
	public Long pesquisarFrequenciaAprazamentoCount(String objPesquisa){
		return this.mpmTipoFrequenciaAprazamentoDAO.pesquisaFrequenciaAprazamentoCount(objPesquisa);
	}
	@Override
	public List<AfaFormaDosagemVO> pesquisaFormaDosagem(Integer matCodigo){
		return this.afaFormaDosagemDAO.pesquisarFormaDosagem(matCodigo);
	}
	@Override
	public List<AfaTipoVelocAdministracoes> pesquisaTipoVelocAdministracoes(){
		return this.afaTipoVelocAdministracoesDAO.pesquisarTipoVelocAdministracoes();
	}
	@Override
	public AfaMedicamento obterAfaMedicamentoPorChavePrimaria(Integer madMatCodigo){
		return this.afaMedicamentoDAO.obterPorChavePrimaria(madMatCodigo);
	}
	@Override
	public AfaFormaDosagem obterAfaFormaDosagemPorChavePrimaria(Integer fdsSeq){
		return this.afaFormaDosagemDAO.obterPorChavePrimaria(fdsSeq);
	}	
	@Override
	public void inserirMedicamento(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, Integer vpsSeq, ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionado, List<ParametroDoseUnidadeVO> listaParam, List<ParametroDoseUnidadeVO> listaParamExcluidos) throws ApplicationBusinessException{
		this.mptProtocoloMedicamentoRN.inserirMedicamento(mptProtocoloMedicamentos, mptProtocoloItemMedicamentos, vpsSeq, parametroSelecionado, listaParam, listaParamExcluidos);
	}
	
	@Override
	public List<String> pesquisaInformacaoFormacologicaAux(Integer madMatCodigo){
		return this.afaItemGrupoMedicamentoDAO.obterInformacaoFormacologicaMedicamentosAux(madMatCodigo);
	}
	
	@Override
	public List<ProtocoloAssociadoVO> listarProtocolosAssociacao(Integer firstResult, Integer maxResults, String orderProperty,	boolean asc, Integer seqProtocolo) {		
		return mptProtocoloAssociacaoDAO.listarProtocolosRelacionados(firstResult, maxResults, orderProperty, asc, seqProtocolo);
	}

	@Override
	public Long listarProtocolosAssociacaoCount(Integer seqProtocolo) {		 
		return mptProtocoloAssociacaoDAO.listarProtocolosRelacionadosCount(seqProtocolo);
	}
	
	@Override
	public List<SituacaoVersaoProtocoloVO> obterSituacaoVersaoProtocoloSelecionado(Integer seqProtocolo){
		return mptVersaoProtocoloSessaoDAO.obterSituacaoVersaoProtocoloSelecionado(seqProtocolo);
	}
	
	@Override
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> consultaCuidadosPorVersaoProtocolo(Integer seqVersaoProtocolo) {
		return mptProtocoloCuidadosDAO.consultaCuidadosPorVersaoProtocolo(seqVersaoProtocolo);
	}
	
	@Override
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarSolucoesPorVersaoProtocolo(Integer seqVersaoProtocoloSessao) {
		return mptProtocoloMedicamentosDAO.pesquisarSolucoesPorVersaoProtocolo(seqVersaoProtocoloSessao);
	}
	
	@Override
	public MptProtocoloMedicamentos obterMedicamentoPorChavePrimaria(Long seq) {
		return mptProtocoloMedicamentosDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public MptProtocoloItemMedicamentos obterItemMedicamentoPorChavePrimaria(Long seq) {
		return mptProtocoloItemMedicamentosDAO.obterPorChavePrimaria(seq);
	}
	
	public MedicamentosVO obterMedicamento(String objPesquisa, Boolean padronizacao, Integer matCodigo){
		return this.afaMedicamentoDAO.obterMedicamento(objPesquisa, padronizacao, matCodigo);
	}
	
	public AfaFormaDosagemVO obterFormaDosagem(Integer seq){
		return this.afaFormaDosagemDAO.obterFormaDosagem(seq);
	}
	
	public AfaViaAdministracao obterVia(String sigla){
		return this.afaViaAdministracaoDAO.obterPorChavePrimaria(sigla);
	}
	
	public MpmTipoFrequenciaAprazamento obterAprazamento(Short seq){
		return this.mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(seq);
	}
	
	public AfaTipoVelocAdministracoes obterUnidadeInfusao(Short seq){
		return this.afaTipoVelocAdministracoesDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public void moverOrdemCima(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO anterior) {
		procedimentoTerapeuticoON.moverOrdemCima(selecionado, anterior);
	}
	
	@Override
	public void moverOrdemBaixo(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO posterior) {
		procedimentoTerapeuticoON.moverOrdemBaixo(selecionado, posterior);
	}
	
	@Override
	public void fazerCheckCelula(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia) {
		procedimentoTerapeuticoRN.fazerCheckCelula(vo, dia);
	}
	
	@Override
	public MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDia(Long ptmSeq, Short dia) {
		return mptProtocoloMedicamentosDiaDAO.obterProtocoloMedicamentosDiaPorPtmSeqDia(ptmSeq, dia);
	}
	
	@Override
	public MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(Long ptmSeq, Short dia) {
		return mptProtocoloMedicamentosDiaDAO.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(ptmSeq, dia);
	}
	
	@Override
	public MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDia(Integer pcuSeq, Short dia) {
		return mptProtocoloCuidadosDiaDAO.obterProtocoloCuidadosDiaPorPcuSeqDia(pcuSeq, dia);
	}
	
	@Override
	public MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(Integer pcuSeq, Short dia) {
		return mptProtocoloCuidadosDiaDAO.obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(pcuSeq, dia);
	}
	
	@Override
	public List<ListaAfaDescMedicamentoTipoUsoMedicamentoVO> listarSbMedicamentos(String param, Boolean indPadronizado) {
		return vAfaDescrMdtoDAO.listarSbMedicamentos(param, indPadronizado);
	}

	@Override
	public Long listarSbMedicamentosCount(String param, Boolean indPadronizado) {
		return vAfaDescrMdtoDAO.listarSbMedicamentosCount(param, indPadronizado);
	}

	@Override
	public List<AfaViaAdministracao> listarSbViaAdministracaoMedicamento(String param, List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias) {
		return afaViaAdministracaoDAO.listarSbViaAdministracaoMedicamento(param, listaMedicamentos, todasVias);
	}

	@Override
	public Long listarSbViaAdministracaoMedicamentoCount(String param, List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias) {
		return afaViaAdministracaoDAO.listarSbViaAdministracaoMedicamentoCount(param, listaMedicamentos, todasVias);
	}

	@Override
	public List<VMpmDosagem> listarComboUnidade(Integer codMedicamento) {
		return vMpmDosagemDAO.listarUnidadeDosagemMedicamento(codMedicamento);
	}

	@Override
	public List<MpmTipoFrequenciaAprazamento> listarSuggestionTipoFrequenciaAprazamento(String param) {
		return mpmTipoFrequenciaAprazamentoDAO.listarSuggestionTipoFrequenciaAprazamento(param);
	}

	@Override
	public Long listarSuggestionTipoFrequenciaAprazamentoCount(String param) {
		return mpmTipoFrequenciaAprazamentoDAO.listarSuggestionTipoFrequenciaAprazamentoCount(param);
	}

	@Override
	public List<AfaTipoVelocAdministracoes> listarComboUnidadeInfusao() {
		return afaTipoVelocAdministracoesDAO.obterListaTiposVelocidadeAdministracao();
	}

	@Override
	public List<ProtocoloItensMedicamentoVO> listarItensMedicamentoProtocolo(Long codSolucao) {
		return cadastroSolucaoProtocoloRN.listarItensMedicamento(codSolucao);
	}

	@Override
	public MptProtocoloMedicamentos buscarSolucaoParaEdicao(Long codSolucao) {
		return procedimentoTerapeuticoRN.buscarSolucaoParaEdicao(codSolucao);
	}

	@Override
	public BigDecimal calcularValorVolumeMl(ProtocoloItensMedicamentoVO protocoloItensMedicamentoVO) {
		return cadastroSolucaoProtocoloRN.obterVolumeMlMedicamento(protocoloItensMedicamentoVO);
	}

	@Override
	public VMpmDosagem buscarMpmDosagemPorSeqMedicamentoSeqDosagem(Integer medMatCodigo, Integer seqDose) {
		return vMpmDosagemDAO.buscarMpmDosagemPorSeqMedicamentoSeqDosagem(medMatCodigo, seqDose);
	}

	@Override
	public MpmUnidadeMedidaMedica obterUnidadeMedidaPorSeq(Integer seqDosagem, Integer seqMedicamento) {
		return mpmUnidadeMedidaMedicaDAO.obterPorSeqDosagemSeqMedicamento(seqDosagem, seqMedicamento);
	}

	@Override
	public AfaDiluicaoMdto buscarAfaDiluicaoMdtoPorMatCodigoEData(Integer medMatCodigo, Date date) {
		return afaDiluicaoMdtoDAO.buscarDiluicaoMdtoPorCodMaterialEData(medMatCodigo, date);
	}

	@Override
	public void verificarInsercaoSolucao(MptProtocoloMedicamentos solucao, List<ProtocoloItensMedicamentoVO> listaMedicamentos) throws ApplicationBusinessException {
		cadastroSolucaoProtocoloRN.verificarInsercaoSolucao(solucao, listaMedicamentos);
	}

	@Override
	public void persistirSolucao(MptProtocoloMedicamentos solucao) {
		mptProtocoloMedicamentosDAO.persistirSolucao(solucao);
	}

	@Override
	public void persistirItemMedicamentoSolucao(MptProtocoloItemMedicamentos itemMedicamento) {
		mptProtocoloItemMedicamentosDAO.persistir(itemMedicamento);
	}

	@Override
	public void persistirSolucaoEditada(MptProtocoloMedicamentos solucao) {
		mptProtocoloMedicamentosDAO.merge(solucao);
	}

	@Override
	public MptVersaoProtocoloSessao obterVersaoProtocoloSessaoPorSeq(Integer seq) {
		return mptVersaoProtocoloSessaoDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public void excluirProtocoloItemMedicamentoDaSolucao(Long ptmSeq) {
		cadastroSolucaoProtocoloRN.excluirItemMedicamento(ptmSeq);
	}

	@Override
	public MpmUnidadeMedidaMedica obterUnidadeMedidaMedicamentoPorSigla(String ummDescricao) {
		return mpmUnidadeMedidaMedicaDAO.obterUnidadesMedidaMedicaPelaSigla(ummDescricao);
	}

	@Override
	public List<MptProtocoloMedicamentosDia> obterProtocoloMdtoDiaModificado (Long ptmSeq) {
		return mptProtocoloMedicamentosDiaDAO.verificarExisteDiaMarcadoParaProtocolo(ptmSeq);
	}

	@Override
	public void atualizarProtocoloSolucaoDia(MptProtocoloMedicamentosDia solucao) {
		mptProtocoloMedicamentosDiaDAO.atualizar(solucao);
	}
	
	@Override
	public void persistirRelacionamento(MptProtocoloAssociacao associacao) {
		mptProtocoloAssociacaoDAO.persistir(associacao);		
	}

	@Override
	public Boolean verificarStatusProtocolo(Integer seqProtocolo, Integer seqVersaoProtocoloSessao) {		 
		return mptProtocoloSessaoDAO.verificarStatusProtocolo(seqProtocolo, seqVersaoProtocoloSessao);
	}	

	@Override
	public MptProtocoloSessao buscarProtocoloPesquisa(Integer seqProtocolo) {		 
		return mptProtocoloSessaoDAO.obterOriginal(seqProtocolo);
	}

	@Override
	public List<MptProtocoloAssociacao> buscarAssociacoes(Integer seqProtocolo) {
		return mptProtocoloAssociacaoDAO.buscarAssociacoes(seqProtocolo); 
	}

	@Override
	public Integer gerarNovoAgrupador() {		 
		return mptProtocoloAssociacaoDAO.gerarNovoAgrupador();
	}

	@Override
	public void excluirRelacionamento(Integer seq) {
		this.mptProtocoloAssociacaoDAO.removerPorId(seq);		
	}

	@Override
	public void excluirCuidados(Integer seqCuidadoExclusao) {
		procedimentoTerapeuticoRN.removerDiasCuidados(seqCuidadoExclusao);
		mptProtocoloCuidadosDAO.removerPorId(seqCuidadoExclusao);		
	}

	@Override
	public MptProtocoloCuidados obterCuidadoParaEdicao(Integer seqEdicao) {		 
		return this.mptProtocoloCuidadosDAO.obterOriginal(seqEdicao);
	}

	@Override
	public void atualizarCuidadoProtocolo(MptProtocoloCuidados mptProtocoloCuidados) {
		this.mptProtocoloCuidadosDAO.merge(mptProtocoloCuidados);		
	}	

	@Override
	public List<MptProtocoloCuidadosDia> verificarDiaCuidado(Integer seqEdicao) {		 
		return mptProtocoloCuidadosDiaDAO.verificarDiaCuidado(seqEdicao);
	}

	@Override
	public void alterarDias(MptProtocoloCuidadosDia item) {
		this.mptProtocoloCuidadosDiaDAO.merge(item);		
	}

	@Override
	public MpmCuidadoUsual obterCuidadoPorSeq(Integer cduSeq) {		 
		return mpmCuidadoUsualDAO.obterCuidadoUsual(cduSeq);
	}

	@Override
	public void atualizarMptProtocoloCuidadosDia(MptProtocoloCuidadosDia diaEdicao) {
		mptProtocoloCuidadosDiaDAO.merge(diaEdicao);		
	}

	@Override		
	public void atualizarTodosDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia) {
		procedimentoTerapeuticoRN.atualizarTodosDiasModificadosMedicamentos(mptProtocoloMedicamentos, listaMptProtocoloMedicamentosDia);
	}
	@Override
	public void atualizarDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia) {
		procedimentoTerapeuticoRN.atualizarDiasModificadosMedicamentos(mptProtocoloMedicamentos, listaMptProtocoloMedicamentosDia);
	}
	@Override
	public void atualizarDiaModificadoMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloMedicamentosDia listaMptProtocoloMedicamentosDia) {
		procedimentoTerapeuticoRN.atualizarDiaModificadoMedicamentos(mptProtocoloMedicamentos, listaMptProtocoloMedicamentosDia);
	}
		
	@Override
	public NovaVersaoProtocoloVO carregarItensVersaoProtocoloPorVpsSeq(Integer versaoProtocoloSeq) {
		return mptVersaoProtocoloSessaoDAO.carregarItensVersaoProtocoloPorVpsSeq(versaoProtocoloSeq);
	}

	@Override
	public Integer carregarNumeroUltimaVersaoPorProtocoloSeq(Integer seqProtocoloSessao) {
		return mptVersaoProtocoloSessaoDAO.carregarNumeroUltimaVersaoPorProtocoloSeq(seqProtocoloSessao);
	}

	@Override
	public Boolean verificarExistenciaProtocolo(Integer seqProtocoloSessao) {		 
		return mptProtocoloAssociacaoDAO.verificarExistenciaProtocolo(seqProtocoloSessao);
	}
	
	public List<AfaMedicamento> pesquisaDiluente(Integer medMatCodigo){
		return this.afaMedicamentoDAO.pesquisaDiluente(medMatCodigo);
	}
	@Override
	public MptVersaoProtocoloSessao inserirNovaVersaoProtocoloSessao(Integer seqProtocolo, Integer versao, Integer qtdCiclos, DominioSituacaoProtocolo indSituacao, 
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes,
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException {
		return mptProtocoloSessaoRN.inserirNovaVersaoProtocoloSessao(seqProtocolo, versao, qtdCiclos, indSituacao, listaProtocoloMedicamentosVO, listaProtocoloSolucoes, listaProtocoloCuidados, diasTratamento);
	}

	@Override
	public void atualizarCampoOrdem(List<ProtocoloMedicamentoSolucaoCuidadoVO> lista, Short ordem) {
		procedimentoTerapeuticoRN.atualizarCampoOrdem(lista, ordem);		
	}

	@Override
	public boolean verificarExistenciaOutraVersaoProtocoloSessao(Integer protocoloSessaoSeq, Short tipoSessaoSeq, Integer vpsSeq) {
		return mptVersaoProtocoloSessaoDAO.verificarExistenciaOutraVersaoProtocoloSessao(protocoloSessaoSeq, tipoSessaoSeq, vpsSeq);
	}

	@Override
	public List<MptProtocoloAssociacao> buscarAssociacoesPorAgrupador(Integer agrupador) {
		return this.mptProtocoloAssociacaoDAO.buscarAssociacoesPorAgrupador(agrupador);
	}
	
	@Override
	public void validarCamposObrigatoriosParametroDose(ParametroDoseUnidadeVO paramDose, Boolean desabilitarCampoUnidade) throws BaseListException {
		cadastroSolucaoProtocoloRN.validarCamposObrigatoriosParametroDose(paramDose, desabilitarCampoUnidade);
	}

	@Override
	public Boolean validarAdicaoParametroDoseParteUm(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) {
		return cadastroSolucaoProtocoloRN.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
	}

	@Override
	public void validarPesoOuIdadeParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException {
		cadastroSolucaoProtocoloRN.validarPesoOuIdadeParamDose(listaParam, paramDoseVO);
	}

	@Override
	public void validarSobreposicaoDeFaixaParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException {
		cadastroSolucaoProtocoloRN.validarSobreposicaoDeFaixaParamDose(listaParam, paramDoseVO);
	}

	@Override
	public void persistirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose) {
		mptParamCalculoDosesDAO.persistir(calculoDose);
	}

	@Override
	public void atualizarParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose) {
		mptParamCalculoDosesDAO.atualizar(calculoDose);
	}

	@Override
	public List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamentoSolucao(ProtocoloItensMedicamentoVO medicamentoSelecionado) {
		return cadastroSolucaoProtocoloRN.preCarregarListaParametroDoseMedicamentoSolucao(medicamentoSelecionado);
	}

	@Override
	public void preInserirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose) {
		cadastroSolucaoProtocoloRN.preInserirParametroDoseMedicamentoSolucao(calculoDose);
	}

	@Override
	public void desatacharMptProtocoloMdto(MptProtocoloMedicamentos solucao) {
		mptProtocoloMedicamentosDAO.desatachar(solucao);
	}

	@Override
	public List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamento(Integer medMatCodigo, Long seqItemProtocoloMdtos) {
		return cadastroSolucaoProtocoloRN.preCarregarListaParametroDoseMedicamento(medMatCodigo, seqItemProtocoloMdtos);
	}

	@Override
	public void removerParametroDose(ParametroDoseUnidadeVO item) {
		cadastroSolucaoProtocoloRN.removerParametroDose(item);
	}

	@Override
	public void validarCamposObrigatoriosMedicamento(MedicamentosVO medicamentosVO,MptProtocoloMedicamentos mptProtocoloMedicamentos,MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos,
			AfaFormaDosagemVO unidadeSelecionada,List<ParametroDoseUnidadeVO> listaParam,AfaViaAdministracao viaSelecionada,MpmTipoFrequenciaAprazamento aprazamentoSelecionado,
			DominioUnidadeHorasMinutos unidHorasCorrer,	AfaTipoVelocAdministracoes unidadeInfusaoSelecionada) throws BaseListException {
		cadastroSolucaoProtocoloRN.validarCamposObrigatoriosMedicamento(medicamentosVO, mptProtocoloMedicamentos, mptProtocoloItemMedicamentos, unidadeSelecionada, listaParam,
				viaSelecionada, aprazamentoSelecionado, unidHorasCorrer, unidadeInfusaoSelecionada);
	}
	
	@Override
	public List<HomologarProtocoloVO> obterListaMedicamentosProtocolo(Integer seqVersaoProtocoloSessao) {
		return mptProtocoloMedicamentosDAO.obterListaMedicamentosProtocolo(seqVersaoProtocoloSessao);
	}

	@Override
	public void gravarHomologacaoProtocolo(Integer vpsSeq, String justificativa, DominioSituacaoProtocolo situacao, 
			List<HomologarProtocoloVO> listaMedicamentosProtocolo) throws ApplicationBusinessException {
		procedimentoTerapeuticoRN.gravarHomologacaoProtocolo(vpsSeq, justificativa, situacao, listaMedicamentosProtocolo);
	}
	

	@Override
	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO obterSbMedicamentos(Integer medMatCodigo, String descricaoMat) {		 
		return this.vAfaDescrMdtoDAO.obterSbMedicamentos(medMatCodigo, descricaoMat);
	}
	
	@Override
	public void alterarSolucao(MptProtocoloMedicamentos solucao, List<ProtocoloItensMedicamentoVO> listaMedicamentos, List<ProtocoloItensMedicamentoVO> listaMedicamentosExclusao) throws ApplicationBusinessException {
		mptProtocoloMedicamentoRN.alterarSolucao(solucao, listaMedicamentos, listaMedicamentosExclusao);
	}

	@Override
	public MptProtocoloMedicamentos obterMedicamentoPorId(Long seq) {		 
		return mptProtocoloMedicamentosDAO.obterOriginal(seq);
	}

	@Override
	public List<AfaFormaDosagemVO> pesquisarFormaDosagemParametroDose(Integer medMatCodigo) {
		return afaFormaDosagemDAO.pesquisarFormaDosagemParamDose(medMatCodigo);
	}

	@Override
	public void validarPesoOuIdadeMinimoMaiorQueMaximo(ParametroDoseUnidadeVO paramDose) throws ApplicationBusinessException {
		cadastroSolucaoProtocoloRN.validarPesoOuIdadeMinimoMaiorQueMaximo(paramDose);
	}

	@Override
	public void excluirParametroDoseMedicamento(Integer seq) {
		mptParamCalculoDosesDAO.removerPorId(seq);
	}

	@Override
	public void excluirProtocoloSolucaoPorSeq(Long pimSeq) {
		mptProtocoloItemMedicamentosDAO.removerPorId(pimSeq);
	}
}