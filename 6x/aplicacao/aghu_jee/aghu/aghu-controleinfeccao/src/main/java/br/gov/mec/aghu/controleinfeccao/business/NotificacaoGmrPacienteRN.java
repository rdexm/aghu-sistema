package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciAntimicrobianosDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciBacteriaMultirDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * RN de #37928 - Lista de Germes Multirresistentes do Paciente
 * 
 * @author aghu
 *
 */
@Stateless
public class NotificacaoGmrPacienteRN extends BaseBusiness {

	private static final long serialVersionUID = 3877552185939552447L;

	private static final Log LOG = LogFactory.getLog(NotificacaoGmrPacienteRN.class);

	private static final String SIGLA_LEITO = "L:";
	private static final String SIGLA_QUARTO = "Q:";

	@Inject
	private MciBacteriaMultirDAO mciBacteriaMultirDAO;

	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;

	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@Inject
	private MciAntimicrobianosDAO mciAntimicrobianosDAO;
	
	@Inject
	private EmailUtil emailUtil;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	
	
	private enum NotificacaoGmrPacienteRNExceptionCode implements BusinessExceptionCode {
		MCI_01037;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * P2 -
	 * 
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscarLocalizacaoPacienteCCIH(Integer pacCodigo) throws ApplicationBusinessException {
		Integer ultimoAtdPac = this.aghAtendimentoDAO.obterUltimoSeqAtdPorPaciente(pacCodigo);
		String localizacao = null;
		// Chamada para ORADB FUNCTION MPMC_LOCAL_PAC_ATD
		localizacao = (ultimoAtdPac == null) ? null : this.prescricaoMedicaFacade.obterLocalPaciente(ultimoAtdPac);
		return localizacao;
	}

	/**
	 * P3 - FORMS P_BUSCA_DT_INTERNACAO
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public Date obterDataUltimaInternacaoPaciente(Integer pacCodigo) {
		AinInternacao internacao = this.internacaoFacade.obrterInternacaoPorPacienteInternado(pacCodigo);
		return internacao == null ? null : internacao.getDthrInternacao();
	}

	/**
	 * RN2
	 * 
	 * @param pacCodigo
	 * @param ambSeq
	 * @param bmrSeq
	 */
	public void criarNotificacaoGMR(Integer pacCodigo, Integer ambSeq, Integer bmrSeq) {
		MciNotificacaoGmr entity = new MciNotificacaoGmr();
		entity.setPaciente(this.aipPacientesDAO.obterPorChavePrimaria(pacCodigo));
		entity.setMciAntimicrobianos(ambSeq == null ? null : this.mciAntimicrobianosDAO.obterPorChavePrimaria(ambSeq));
		entity.setMciBacteriaMultir(this.mciBacteriaMultirDAO.obterPorChavePrimaria(bmrSeq));
		entity.setNotificacaoAtiva(Boolean.TRUE);
		entity.setCriadoEm(new Date());
		entity.setRapServidores(this.servidorLogadoFacade.obterServidorLogado());
		this.mciNotificacaoGmrDAO.persistir(entity);
	}

	/**
	 * RN3
	 * 
	 * @param seq
	 */
	public void desativarNotificacao(Integer seq) {
		MciNotificacaoGmr entity = this.mciNotificacaoGmrDAO.obterPorChavePrimaria(seq);
		if (entity != null) {
			entity.setNotificacaoAtiva(Boolean.FALSE);
			entity.setServidorAltera(this.servidorLogadoFacade.obterServidorLogado());
			entity.setAlteradoEm(new Date());
		}
		this.mciNotificacaoGmrDAO.atualizar(entity);
	}

	/**
	 * ORADB FUNCION MCIC_LOCAL_AIP_PAC
	 * 
	 * @param codigoPaciente
	 */
	public String obterLocalPaciente(final Integer codigoPaciente) {
		// CURSOR C_PAC
		AghAtendimentos pacienteAtendimento = this.aghAtendimentoDAO.obterUltimoAtendimentoPacienteListaGermes(codigoPaciente);
		if (pacienteAtendimento != null) {
			final Date dataUltimaInternacao = pacienteAtendimento.getDthrInicio();
			final Date dataUltimaAlta = pacienteAtendimento.getDthrFim();
			if (dataUltimaInternacao == null) {
				return null;
			} else {
				if (dataUltimaAlta == null || (dataUltimaAlta != null && DateUtil.validaDataMenor(dataUltimaAlta, dataUltimaInternacao))) {
					if (pacienteAtendimento.getLeito() != null) {
						return SIGLA_LEITO + pacienteAtendimento.getLeito().getLeitoID();
					} else {
						if (pacienteAtendimento.getQuarto() != null) {
							return SIGLA_QUARTO + pacienteAtendimento.getQuarto().getNumero();
						} else {
							if (pacienteAtendimento.getUnidadeFuncional() != null) {
								if (pacienteAtendimento.getUnidadeFuncional() == null) {
									return null;
								} else {
									// CURSOR C_UNID2: Implícito
									return StringUtils.substring(pacienteAtendimento.getUnidadeFuncional().getSigla(), 1, 5);
								}
							} else {
								return null;
							}
						}
					}
				} else if (dataUltimaAlta != null && DateUtil.validaDataMaiorIgual(dataUltimaAlta, dataUltimaInternacao)) {
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 * FUNCION ORADB MCIP_ENVIA_EMAIL_GMR
	 * 
	 * @param origem
	 * @param pacCodigo
	 * @param prontuario
	 * @param leitoID
	 * @param unfSeq
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void enviaEmailGmr(DominioOrigemAtendimento origem, Integer pacCodigo, Integer prontuario, String leitoID, Short unfSeq) throws ApplicationBusinessException{
		
		String  vBacteria = "", vDescricao = "", nomePaciente = "";
		
			List<MciBacteriaMultir>	gmr =  this.controleInfeccaoFacade.pesquisarDescricaoBacteria(pacCodigo);
			String unid = this.controleInfeccaoFacade.obterUnidFuncionalDescAndar(unfSeq);
	
			
			if(unid != null){
				vDescricao = unid;
			}
				if((origem != null && origem != DominioOrigemAtendimento.A && origem != DominioOrigemAtendimento.N) 
						&& verificaPacNotifGrm(pacCodigo)){
     					
					this.obterParametro(AghuParametrosEnum.P_EMAIL_NOTIF_GMR);
					
					if(gmr != null && !gmr.isEmpty()){
						for(MciBacteriaMultir cGmr : gmr){
							if(vBacteria.equals("")){ 
								vBacteria = cGmr.getDescricao();
							}else{
								vBacteria = vBacteria.concat(", ").concat(cGmr.getDescricao());
							}
						}
					}
					
					if(leitoID == null){
						vDescricao = "Unidade: ".concat(vDescricao);
					}else{
						vDescricao = "Leito: ".concat(vDescricao);
					}

					AipPacientes paciente = aipPacientesDAO.obterNomePacientePorCodigo(pacCodigo);
					
					if(paciente != null && paciente.getNome() != null){
						nomePaciente = paciente.getNome();
					}
					
					String hcpa = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU).getVlrTexto();
					String remetente = "correio@hcpa.ufrgs.br";
					String destinatario = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_NOTIF_GMR).getVlrTexto();
					destinatario = destinatario.replace(";", " ");
					String assunto = "NOTIFICACAO GMR ".concat(hcpa);
					
					String corpo = "Paciente "+prontuario+" - ".concat(nomePaciente)
							.concat(" com germe multirresistente em atendimento no HCPA.").concat("\n").concat(vDescricao).concat("\n").concat(" Germe(s): "+vBacteria+".");
					
					emailUtil.enviaEmail(remetente, destinatario, null, assunto, corpo);
				}
	}
	
	public Boolean verificaPacNotifGrm(Integer pacCodigo){
		Boolean vNotif = false;
			List<MciNotificacaoGmr>	pacNotificacao =  this.controleInfeccaoFacade.pesquisarNotificacaoAtiva(pacCodigo);
				if(!pacNotificacao.isEmpty()){
					vNotif = true;
				}
		return vNotif;
	}
		
	public void obterParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {		
		if(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_NOTIF_GMR).getVlrTexto() == null){
			throw new ApplicationBusinessException(NotificacaoGmrPacienteRNExceptionCode.MCI_01037);
		}
	}
}
