package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.RetornoConsultoriaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RetornoConsultoriaON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7628522591669372734L;

	@EJB
	private RespostasConsultoriaON respostasConsultoriaON;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuaroOnlineFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private MpmPrescricaoMedicaDAO prescricaoMedicaDAO;

	@Inject
	private MpmCidAtendimentoDAO cidAtendimentoDAO;

	@Inject
	private MpmSolicitacaoConsultoriaDAO solicitacaoConsultoriaDAO;
	
	@Inject
	private MpmRespostaConsultoriaDAO respostaConsultoriaDAO;

	private static final Log LOG = LogFactory.getLog(RetornoConsultoriaON.class);
	
	private static final String NEWLINE = System.getProperty("line.separator");

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<RetornoConsultoriaVO> pesquisarRetornoConsultorias(
			Integer atdSeq, Integer scnSeq) throws ApplicationBusinessException {
		StringBuilder titulo = new StringBuilder("");
		List<MpmSolicitacaoConsultoria> solicitacoes = new ArrayList<MpmSolicitacaoConsultoria>();
		RetornoConsultoriaVO vo = null;
		AghAtendimentos atendimento = null;
		String dataEmissao = DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		List<RetornoConsultoriaVO> retorno = new ArrayList<RetornoConsultoriaVO>();

		solicitacoes = this.solicitacaoConsultoriaDAO.listarSolicitacaoRetornoConsultoria(atdSeq, scnSeq);
		
		for (MpmSolicitacaoConsultoria solicitacao : solicitacoes) {			
			vo = new RetornoConsultoriaVO();
			
			atendimento = aghuFacade.obterAghAtendimentosPorSeq(atdSeq);
			vo.setEspecialidadeDescricao(solicitacao.getEspecialidade().getNomeEspecialidade());
			vo.setSiglaEspecialidade(solicitacao.getEspecialidade().getSigla());
			
			// FC5
			if (solicitacao.getServidorValidacao() != null) {
				vo.setNomeSolicitante(obterNomeSolicitante(solicitacao.getServidorValidacao().getId().getMatricula(), solicitacao.getServidorValidacao().getId().getVinCodigo()));
			}

			vo.setDataHoraSolicitacao(DateUtil.obterDataFormatada(solicitacao.getDthrSolicitada(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

			//FC3
			List<MpmPrescricaoMedica> prescricaoMedica = prescricaoMedicaDAO.pesquisaPrescricoesMedicasPorAtendimentoDataSolicitacao(atdSeq, solicitacao.getDthrSolicitada());
			
			if (!prescricaoMedica.isEmpty()) {
				vo.setPrescricao(prescricaoMedica.get(0).getId().getSeq().toString());
			}

			vo.setProntuario(CoreUtil.formataProntuario(atendimento.getPaciente().getProntuario()));
			vo.setNomePaciente(atendimento.getPaciente().getNome());
			vo.setDataNascimento(DateUtil.obterDataFormatada(atendimento.getPaciente().getDtNascimento(), DateConstants.DATE_PATTERN_DDMMYYYY));
			//Mpmc_ida_ano_mes_dia
			vo.setIdade(blocoCirurgicoFacade.obterIdadePorDataNascimento(atendimento.getPaciente().getDtNascimento()));
			vo.setSexo(atendimento.getPaciente().getSexo().getDescricao());
			//mpmc_localiza_pac
			vo.setLocalizacao(pacienteFacade.obterLocalizacaoPaciente(atendimento.getLeito(), atendimento.getQuarto(), atendimento.getUnidadeFuncional()));
			vo.setDataUltimaInternacao(DateUtil.obterDataFormatada(atendimento.getPaciente().getDtUltInternacao(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			vo.setClinicaDescricao(solicitacao.getEspecialidade().getClinica().getDescricao());
			vo.setDiagnostico(obterDiagnostico(atdSeq));
			vo.setMotivo(solicitacao.getMotivo());
			vo.setDataHoraConhecimento(DateUtil.obterDataFormatada(solicitacao.getDthrPrimeiraConsulta(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			//RF02
			vo.setResposta(respostasConsultoriaON.pesquisarRespostasConsultoriaPorAtdSeqConsultoria(atdSeq, scnSeq, Integer.valueOf("2")));
			//FC4
			vo.setAssinaturaResponsavel(obterAssinaturaResponsavel(atdSeq, scnSeq));

			//FC6
			if (atendimento != null) {
				if (atendimento.getProntuario() != null && atendimento.getProntuario() > 90000000) {
					if (atendimento.getGsoPacCodigo() != null) {
						AipPacientes mae = pacienteFacade.obterPaciente(atendimento.getGsoPacCodigo());
						
						if (mae != null && mae.getProntuario() != null) {
							vo.setProntuarioMae("Mãe: " + CoreUtil.formataProntuario(mae.getProntuario()));
						}
					}
				} else {
					if (atendimento.getProntuario() != null) {
						vo.setProntuarioMae(CoreUtil.formataProntuario(atendimento.getProntuario()));
					}
				}
			}
			
			vo.setDataHoraEmissao(dataEmissao);
			titulo = new StringBuilder();
			
			titulo.append("RETORNO DE CONSULTORIA");
			if (solicitacao.getIndConcluida().equals(DominioIndConcluidaSolicitacaoConsultoria.A)) {
				titulo.append(" EM ACOMPANHAMENTO");
			} else if (solicitacao.getIndConcluida().equals(DominioIndConcluidaSolicitacaoConsultoria.S)) {
				titulo.append(" CONCLUÍDA");
			}

			vo.setTitulo(titulo.toString());

			retorno.add(vo);
		}

		return retorno;
	}
	
	public String obterNomeSolicitante(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		StringBuilder builder = new StringBuilder();
		VRapServidorConselho servidor = registroColaboradorFacade.obterVRapServidorConselhoPeloId(matricula, vinCodigo, null);

		if (servidor != null && servidor.getNroRegConselho() != null) {
			RapServidores colaborador = registroColaboradorFacade.obterRapServidor(new RapServidoresId(matricula, vinCodigo));
			if (colaborador != null && colaborador.getUsuario() != null && permissionService.usuarioTemPermissao(colaborador.getUsuario(), "prescricaoMedica", "confirmar")) {
				
				String nomeFormatado = this.prontuaroOnlineFacade.formataNomeProf(matricula, vinCodigo);
				if (nomeFormatado != null && !nomeFormatado.isEmpty()) {
					return nomeFormatado;
					
				} else {
					List<VRapServidorConselho> conselhos =  registroColaboradorFacade.pesquisarConselhoPorMatriculaVinculo(matricula, vinCodigo);
					
					if (!conselhos.isEmpty()) {
						VRapServidorConselho conselho = conselhos.get(0);
						builder.append(conselho.getNome()).append("   ")
						.append(conselho.getSigla()).append(": ")
						.append(conselho.getNroRegConselho());
					}
				}
			}
		}

		return builder.toString();
	}

	public String obterDiagnostico(Integer atdSeq) {
		StringBuilder builder = new StringBuilder("");
		List<MpmCidAtendimento> cidAtendimentos = cidAtendimentoDAO.listarCidAtendimentoPorAtdSeq(atdSeq);

		if (!cidAtendimentos.isEmpty()) {
			MpmCidAtendimento cidAtendimento = cidAtendimentos.get(0);
			if (cidAtendimento.getCid() != null && cidAtendimento.getCid().getCid() == null) {
				builder.append(StringUtils.rightPad(cidAtendimento.getCid().getCodigo() != null ? cidAtendimento.getCid().getCodigo() : "", 5, ' ')).append(' ');
				builder.append(cidAtendimento.getCid().getDescricao()).append(' ');
			} else {
				builder.append(cidAtendimento.getCid().getCid().getDescricao()).append(' ');
				builder.append(NEWLINE);
				builder.append(StringUtils.rightPad(cidAtendimento.getCid().getCodigo() != null ? cidAtendimento.getCid().getCodigo() : "", 5, ' ')).append(' ');
				builder.append(cidAtendimento.getCid().getDescricao()).append(' ');
			}
									
			if (cidAtendimento.getComplemento() != null) {
				builder.append("- ").append(cidAtendimento.getComplemento());
			}
		}
		
		return builder.toString();
	}
	
	private String obterAssinaturaResponsavel(Integer atdSeq, Integer scnSeq) throws ApplicationBusinessException {
		RapServidores servidor = null;
		StringBuilder builder = new StringBuilder("");
		
		List<MpmRespostaConsultoria> respostas = respostaConsultoriaDAO.listarRespostaConsultoriaPorId(atdSeq, scnSeq);
		if (!respostas.isEmpty() && respostas.get(0).getServidor() != null) {
			servidor = respostas.get(0).getServidor();
			if (servidor != null && servidor.getUsuario() != null && permissionService.usuarioTemPermissao(servidor.getUsuario(), "prescricaoMedica", "confirmar")) {
				
					String nomeFormatado = this.prontuaroOnlineFacade.formataNomeProf(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
				if (nomeFormatado != null && !nomeFormatado.isEmpty()) {
					return nomeFormatado;
					
				} else {
					List<VRapServidorConselho> conselhos =  registroColaboradorFacade.pesquisarConselhoPorMatriculaVinculo(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
					
					if (!conselhos.isEmpty()) {
						VRapServidorConselho conselho = conselhos.get(0);
						builder.append(conselho.getNome()).append(' ')
						.append(conselho.getSigla()).append(' ')
						.append(conselho.getNroRegConselho());
					}
				}
			}
		}
		return builder.toString();
	}
}