package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MpmSolicitacaoConsultoriaRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpmSolicitacaoConsultoriaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;

	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO; 

	private static final long serialVersionUID = 7245912448693429256L;
	
	public enum MpmSolicitacaoConsultoriaRNExceptionCode implements BusinessExceptionCode {
		MPM_02230, NENHUM_REGISTRO_ENCONTRADO_CONSULTORIAS_INTERNACAO, ERRO_ATUALIZAR_SOLICITACAO_CONSULTORIA;
	}
	
	/**
	 * Procedure para buscar a especialidade que o usuário logado é consultor.
	 * 
	 * @ORADB MPMP_POPULA_CONTROL_BLOCK
	 * @return
	 */
	public AghEspecialidades obterEspecialidadePorUsuarioConsultor() {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.pesquisarEspecialidadesPorServidor(servidorLogado);
		
		if (!listaEspecialidades.isEmpty()) {
			return listaEspecialidades.get(0);
		}
		return null;
	}
	
	/**
	 * Procedure para verificar se o profissional logado está habilitado para realizar a pesquisa na especialidade selecionada.
	 * 
	 * @ORADB MPMP_VERIFICA_ACESSO
	 * @param espSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void verificarAcessoProfissionalEspecialidade(Short espSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		boolean possuiPermissao = this.aghuFacade.verificarExisteProfEspecialidadePorServidorEspSeq(servidorLogado, espSeq);
		
		if (!possuiPermissao) {
			throw new ApplicationBusinessException(MpmSolicitacaoConsultoriaRNExceptionCode.MPM_02230);
		}
	}
	
	public List<ConsultoriasInternacaoVO> listarConsultoriasInternacaoPorAtendimento(Short espSeq, Short unfSeq, DominioTipoSolicitacaoConsultoria tipo,
			DominioSimNao urgencia, DominioSituacaoConsultoria situacao) throws ApplicationBusinessException {
		
		List<ConsultoriasInternacaoVO> listaRetorno = this.getMpmSolicitacaoConsultoriaDAO()
				.listarConsultoriasInternacaoPorAtendimento(espSeq, unfSeq, tipo, urgencia, situacao);
		
		if (!listaRetorno.isEmpty()) {
			confirmarAvaliacao(espSeq, tipo, urgencia);
		
			int seqAux = 0;
			for (ConsultoriasInternacaoVO vo : listaRetorno) {
				// Seta o seqAux para tornar o registro único.
				vo.setSeqAux(seqAux);
				seqAux++;
				
				// Busca o convênio
				FatConvenioSaudePlano convSaudePlano = this.getPacienteFacade().obterConvenioSaudePlanoAtendimento(vo.getAtdSeq());
				if (convSaudePlano != null) {
					vo.setConvenio(convSaudePlano.getConvenioSaude().getDescricao());
				}
				// Busca o local do paciente
				AghAtendimentos aghAtendimentos = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdSeq());
				vo.setLocalPac(this.getPrescricaoMedicaFacade().buscarResumoLocalPaciente(aghAtendimentos));

				// Verifica GMR
				if(aghAtendimentos != null && aghAtendimentos.getPaciente() != null){
					vo.setIndGmr(mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(aghAtendimentos.getPaciente().getCodigo()));
				}

			}
			
			// Não é necessário usar a function MPMC_LOCAL_PAC_ATD.
			if (situacao != null && (situacao.equals(DominioSituacaoConsultoria.CO) || situacao.equals(DominioSituacaoConsultoria.A))) {
				Collections.sort(listaRetorno, Collections.reverseOrder(new Comparator<ConsultoriasInternacaoVO>() {
					@Override
					public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
						return o1.getLocalPac().compareTo(o2.getLocalPac());
					}
				}));
				
			} else if (situacao != null && situacao.equals(DominioSituacaoConsultoria.PA)) {
				Collections.sort(listaRetorno, new Comparator<ConsultoriasInternacaoVO>() {
					@Override
					public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
						int order1 = o2.getIndConcluida().toString().compareTo(o1.getIndConcluida().toString());
						int order2 = o1.getCriadoEm().compareTo(o2.getCriadoEm());
						return order1 !=0 ? order1 : order2;
					}
				});
				
			} else {
				Collections.sort(listaRetorno, new Comparator<ConsultoriasInternacaoVO>() {
					@Override
					public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
						return o1.getCriadoEm().compareTo(o2.getCriadoEm());
					}
				});
			}
		} else {
			throw new ApplicationBusinessException(MpmSolicitacaoConsultoriaRNExceptionCode.NENHUM_REGISTRO_ENCONTRADO_CONSULTORIAS_INTERNACAO);
		}
		return listaRetorno;
	}
	
	/**
	 * Procedure para atualizar as consultorias com data/hora de primeira consulta
	 * igual à nulo -> para hora corrente e setar responsável através do usuário logado.
	 * 
	 * @ORADB MPMP_CONFIRMA_AVALIACAO
	 * @param espSeq
	 * @param tipo
	 * @param urgencia
	 */
	public void confirmarAvaliacao(Short espSeq, DominioTipoSolicitacaoConsultoria tipo, DominioSimNao urgencia) {
		
		List<MpmSolicitacaoConsultoria> listConsultorias = this.getMpmSolicitacaoConsultoriaDAO()
				.obterSolicitacoesConsultoriaPorEspSeqTipoUrgencia(espSeq, tipo, urgencia);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listConsultorias) {
			solicitacaoConsultoria.setServidorConsultaVerificacao(servidorLogado);
			solicitacaoConsultoria.setDthrPrimeiraConsulta(new Date());
			
			this.getMpmSolicitacaoConsultoriaDAO().merge(solicitacaoConsultoria);
		}
	}
	
	/** 
	 * #998
	 * @ORADB MPMK_REC_RN.RN_RECP_ATU_SLCT_CNS
	 * Procedure para atualizar a dthr_primeira _consulta na tabela mpm_solicitacao_consultorias caso a mesma for nula.
	 * 
	 * @ORADB MPMK_REC_RN.RN_RECP_ATU_DTHR_RES
	 * Procedure para atualizar data da resposta e colocar nulo na data e servidor de conhecimento na 
	 * tabela mpm_solicitacao_consultorias, atualizar ind_concluida.
	 * 
	 * @param solicitacaoConsultoria
	 * @param mpmRespostaConsultoria
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarSolicitacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria, MpmRespostaConsultoria resposta) throws ApplicationBusinessException {
		
		// @ORADB MPMK_REC_RN.RN_RECP_ATU_SLCT_CNS
		if (solicitacaoConsultoria.getDthrPrimeiraConsulta() == null) {
			solicitacaoConsultoria.setDthrPrimeiraConsulta(resposta.getId().getCriadoEm());
		}
		// @ORADB MPMK_REC_RN.RN_RECP_ATU_DTHR_RES
		solicitacaoConsultoria.setDthrResposta(resposta.getId().getCriadoEm());
		solicitacaoConsultoria.setDthrConhecimentoResposta(null);
		solicitacaoConsultoria.setServidorRespostaVerificacao(null);
		solicitacaoConsultoria.setIndConcluida(
				DominioFinalizacao.A.equals(resposta.getFinalizacao()) ? DominioIndConcluidaSolicitacaoConsultoria.A 
						: DominioIndConcluidaSolicitacaoConsultoria.S);
		
		try{
			this.getMpmSolicitacaoConsultoriaDAO().merge(solicitacaoConsultoria);
		} catch(Exception e){
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(MpmSolicitacaoConsultoriaRNExceptionCode.ERRO_ATUALIZAR_SOLICITACAO_CONSULTORIA);	
		}		
	}

	public void gerarPendenciasSolicitacaoConsultoria(){
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		List<MpmSolicitacaoConsultoria> listaMpmSolicitacaoConsultoria = getMpmSolicitacaoConsultoriaDAO().
				pesquisarSolicitacaoConsultoriaPorServidorEspecialidade(servidorLogado);

		removerPendencias();

		for (MpmSolicitacaoConsultoria solicitacaoConsultoria: listaMpmSolicitacaoConsultoria){
			StringBuilder mensagem = new StringBuilder(55);
			if(solicitacaoConsultoria.getOrigem().equals(DominioOrigemSolicitacaoConsultoria.M)){
				mensagem.append("Consultoria internação em ")
				.append(solicitacaoConsultoria.getEspecialidade().getNomeEspecialidade())
				.append(" para ")
				.append(solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getPaciente().getNome())
				.append(", prontuário: ")
				.append(solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getPaciente().getProntuario())
				.append(", ")
				.append(obterLocalPaciente(solicitacaoConsultoria.getPrescricaoMedica().getAtendimento()))
				.append(obterEquipeAtendimento(solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getServidor()))
				.append(", ")
				.append(solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getEspecialidadeAtendimento().getNomeEspecialidade())
				.append(", em ")
				.append(DateUtil.obterDataFormatada(solicitacaoConsultoria.getCriadoEm(), "dd/MM/yyyy HH:mm"));
				try {
					centralPendenciaFacade.adicionarPendenciaAcao(mensagem.toString(),
							"/prescricaomedica/consultoria/visualizaDadosSolicitacaoConsultoria.xhtml?atdSeq=" + solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getSeq() +"&scnSeq=" + solicitacaoConsultoria.getId().getSeq(), "Consultoria internação",
							servidorLogado, false);
					atualizarDthrPrimeiraConsultaSolicitacaoConsultoria(solicitacaoConsultoria);
				} catch (ApplicationBusinessException e) {
					logError(e);
				}	
			} else if(solicitacaoConsultoria.getOrigem().equals(DominioOrigemSolicitacaoConsultoria.E)) {
				/*
				 * Esta condição foi incluída para solucionar o Incidente AGHU #47970
				 *  
				 *  Quando a origem da solicitação é enfermagem os dados da prescrição associada não estao no banco, desta forma a Liziane e o Fred solicitaram que o 
				 *  atendimento seja buscado diretamente da solicitação e não da prescrição médica (que é a orientação para o restante do sistema).
				 *  
				 */
				BigDecimal retorno = (BigDecimal) this.getMpmSolicitacaoConsultoriaDAO().obterSeqAtendimentoOrigemEnfermagemPorConsultoria(solicitacaoConsultoria.getId().getSeq());
				Integer atdSeq = retorno.intValue();
				AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoParaSolicitacaoConsultoria(atdSeq);
				mensagem.append("Consultoria internação em ")
				.append(solicitacaoConsultoria.getEspecialidade().getNomeEspecialidade())
				.append(" para ")
				.append(atendimento.getPaciente().getNome())
				.append(", prontuário: ")
				.append(atendimento.getPaciente().getProntuario())
				.append(", ")
				.append(obterLocalPaciente(atendimento));
				if(atendimento.getServidor()!= null) {
					mensagem.append(obterEquipeAtendimento(atendimento.getServidor()));
				}
				mensagem.append(", ")
				.append(atendimento.getEspecialidadeAtendimento().getNomeEspecialidade())
				.append(", em ")
				.append(DateUtil.obterDataFormatada(solicitacaoConsultoria.getCriadoEm(), "dd/MM/yyyy HH:mm"));
				try {
					centralPendenciaFacade.adicionarPendenciaAcao(mensagem.toString(),
							"/prescricaomedica/consultoria/visualizaDadosSolicitacaoConsultoria.xhtml?atdSeq=" + atendimento.getSeq() +"&scnSeq=" + solicitacaoConsultoria.getId().getSeq(), "Consultoria internação",
							servidorLogado, false);
					atualizarDthrPrimeiraConsultaSolicitacaoConsultoria(solicitacaoConsultoria);
				} catch (ApplicationBusinessException e) {
					logError(e);
				}
			}
			
		}
		
	}

	private String obterLocalPaciente(AghAtendimentos atendimento) {
		
		StringBuilder local = new StringBuilder(50);
		
		if(atendimento != null){
			if(atendimento.getLeito() != null && atendimento.getLeito().getLeitoID() != null){
				local.append("L: ").append(atendimento.getLeito().getLeitoID());
			} else if (atendimento.getQuarto() != null && atendimento.getQuarto().getNumero() != null){
				local.append("Q: ").append(atendimento.getQuarto().getNumero());
			} else if (atendimento.getUnidadeFuncional() != null && atendimento.getUnidadeFuncional().getAndarAlaDescricao() != null){
				local.append("U: ").append(atendimento.getUnidadeFuncional().getAndarAlaDescricao());
			}
		}
		
		return local.toString();
	}

	private String obterEquipeAtendimento(RapServidores servidor) {
		return servidor != null && servidor.getPessoaFisica() != null && servidor.getPessoaFisica().getNome() != null ?
						" Equipe: " + servidor.getPessoaFisica().getNome() : "";
	}
	
	private void removerPendencias() {
		try {
			String pendencia = "Consultoria internação em";
			for (PendenciaVO pendenciaVO : centralPendenciaFacade.getListaPendencias()) {
				if (pendenciaVO.getMensagem().startsWith(pendencia)) {
					centralPendenciaFacade.excluirPendencia(pendenciaVO.getSeqCaixaPostal());
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(null, e);
		}
	}

	public void atualizarDthrPrimeiraConsultaSolicitacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) throws ApplicationBusinessException {
		
		if (solicitacaoConsultoria.getDthrPrimeiraConsulta() == null) {
			solicitacaoConsultoria.setDthrPrimeiraConsulta(new Date());
			solicitacaoConsultoria.setServidorConsultaVerificacao(getServidorLogadoFacade().obterServidorLogado());
			try{
				this.getMpmSolicitacaoConsultoriaDAO().merge(solicitacaoConsultoria);
			} catch(Exception e){
				LOG.error("Exceção capturada: ", e);
				throw new ApplicationBusinessException(MpmSolicitacaoConsultoriaRNExceptionCode.ERRO_ATUALIZAR_SOLICITACAO_CONSULTORIA);	
			}		
		}
		
	}
	
	public MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public IParametroFacade getParametroFacade()  {
		return parametroFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
}
