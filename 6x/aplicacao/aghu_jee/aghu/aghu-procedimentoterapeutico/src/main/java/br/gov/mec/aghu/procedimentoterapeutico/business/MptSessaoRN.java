package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptControleDispensacao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleDispensacaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptExtratoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -9038521069189468941L;
	
	@Inject
	private MptSessaoDAO mptSessaoDAO;
	
	@Inject
	private MptExtratoSessaoDAO mptExtratoSessaoDAO;
	
	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;
	
	@Inject
	private MptControleDispensacaoDAO mptControleDispensacaoDAO;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * Inseri na tabela de Extrato Sessão.
	 * @param mptSessao
	 */
	private void inserirTabelaExtratoSessao(MptSessao mptSessao) {
		MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
		mptExtratoSessao.setMptSessao(mptSessao);		
		mptExtratoSessao.setIndSituacao(mptSessao.getIndSituacaoSessao());
		mptExtratoSessao.setCriadoEm(new Date());
		mptExtratoSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		mptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}
	
	/**
	 * Atualiza a tabela MptSessao e cria MptExtratoSessao.
	 * @param paciente
	 */
	public void chegouPaciente(ListaPacienteAgendadoVO paciente) {	
		
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		
		if (mptSessao != null){
			if (DominioSituacaoSessao.SSO.equals(mptSessao.getIndSituacaoSessao())){
				mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SEA);			
			}else if (DominioSituacaoSessao.SEA.equals(mptSessao.getIndSituacaoSessao())){
				mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SSO);
			}			
		
			mptSessaoDAO.atualizar(mptSessao);
		
			inserirTabelaExtratoSessao(mptSessao);
		}
	}
	
	/**
	 * Atualiza a tabela MptSessao e cria MptExtratoSessao.
	 * @param paciente
	 */
	public void emAtendimento(ListaPacienteAguardandoAtendimentoVO paciente) {	
		
		//Atualiza a tabela (MPT_SESSAO).
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getSeqSessao());
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SAT);			
		mptSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);
	}
	
	/**
	 * Atualiza a tabela MptSessao e cria MptExtratoSessao.
	 * @param paciente
	 */
	public void voltarAcolhimento(ListaPacienteAguardandoAtendimentoVO parametroSelecionado) {	
		
		//Atualiza a tabela (MPT_SESSAO).
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(parametroSelecionado.getSeqSessao());
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SEA);			
		mptSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);
	}
	
	public List<PrescricaoPacienteVO> obterListaPrescricoesPorPaciente(Integer pacCodigo, Date dataCalculada) {
		List<PrescricaoPacienteVO> listaRetorno = this.mptSessaoDAO.obterListaPrescricoesPorPaciente(pacCodigo, dataCalculada);
		
		List<PrescricaoPacienteVO> listaFinal = new ArrayList<PrescricaoPacienteVO>();
		List<Short> listaCiclos = new ArrayList<Short>();
		List<String> listaResponsavel1 = new ArrayList<String>();
		List<String> listaResponsavel2 = new ArrayList<String>();
		List<Integer> listaCloSeq = new ArrayList<Integer>();
		List<Integer> listaLote = new ArrayList<Integer>();
		for (PrescricaoPacienteVO voAux : listaRetorno) {
			if (!listaCiclos.contains(voAux.getCiclo())
					|| !listaResponsavel1.contains(voAux.getResponsavel1())
					|| !listaResponsavel2.contains(voAux.getResponsavel2())
					|| !listaCloSeq.contains(voAux.getCloSeq())
					|| !listaLote.contains(voAux.getLote())) {
				
				listaFinal.add(voAux);
				listaCiclos.add(voAux.getCiclo());
				listaResponsavel1.add(voAux.getResponsavel1());
				listaResponsavel2.add(voAux.getResponsavel2());
				listaCloSeq.add(voAux.getCloSeq());
				listaLote.add(voAux.getLote());
			}
		}
		
		for (PrescricaoPacienteVO vo : listaFinal) {
			List<MptProtocoloCiclo> protocolos = this.mptProtocoloCicloDAO.buscarProtocoloCiclo(vo.getCloSeq());
			
			String protocolo = null;
			String tituloAnterior = null;
			if (protocolos.size() > 1) {
				for (MptProtocoloCiclo p : protocolos) {
					String titulo = p.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo();
					if (protocolo == null) {
						protocolo = titulo;
						tituloAnterior = titulo;
						
					} else if (!tituloAnterior.equalsIgnoreCase(titulo)) {
						protocolo = protocolo.concat(" - ").concat(titulo);
						tituloAnterior = titulo;
					}
				}
				vo.setProtocolo(protocolo);
			} else if (!protocolos.isEmpty()) {
				MptProtocoloCiclo p = protocolos.get(0);
				if (p.getDescricao() != null) {
					vo.setProtocolo(p.getDescricao());
					
				} else {
					vo.setProtocolo(p.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo());
				}
			}
		}
		for (Iterator<PrescricaoPacienteVO> vo = listaFinal.iterator(); vo.hasNext(); ) {
			PrescricaoPacienteVO item = vo.next();
			List<CadIntervaloTempoVO> listaHorarios = this.listarIntervalosTempoPrescricao(item.getLote(), item.getCloSeq());
			if(listaHorarios.isEmpty()){
				vo.remove();
			}
		}
		return listaFinal;
	}
	
	public List<CadIntervaloTempoVO> listarIntervalosTempoPrescricao(Integer lote, Integer cloSeq) {
		List<CadIntervaloTempoVO> listaRetorno = this.mptSessaoDAO.listarIntervalosTempoPrescricao(lote, cloSeq);
		
		for (CadIntervaloTempoVO vo : listaRetorno) {
			Short minutos = vo.getTempoAdministracao() != null ? vo.getTempoAdministracao() : 0;
			Date tempoCalculado = DateUtil.adicionaMinutos(DateUtil.truncaData(new Date()), minutos.intValue());
			
			vo.setQtdeHoras(tempoCalculado);
			vo.setHoraInicioReferencia(DateUtil.truncaData(new Date()));
			vo.setHoraFimReferencia(tempoCalculado);
			vo.setAgendar(true);
		}
		return listaRetorno;
	}
	
	public void concluirAcolhimento(PacienteAcolhimentoVO paciente) {	
		
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SAA);			
		mptSessaoDAO.atualizar(mptSessao);
		
		MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
		mptExtratoSessao.setMptSessao(mptSessao);		
		mptExtratoSessao.setIndSituacao(mptSessao.getIndSituacaoSessao());
		mptExtratoSessao.setCriadoEm(new Date());
		mptExtratoSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		mptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}
	
	public void voltarParaAgendado(PacienteAcolhimentoVO paciente) {	
		
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SSO);			
		mptSessaoDAO.atualizar(mptSessao);
		
		MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
		mptExtratoSessao.setMptSessao(mptSessao);		
		mptExtratoSessao.setIndSituacao(mptSessao.getIndSituacaoSessao());
		mptExtratoSessao.setCriadoEm(new Date());
		mptExtratoSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		mptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}
	
	public void marcarMedicamentoDomiciliar(PacienteAcolhimentoVO paciente) {	
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		mptSessao.setIndMedicamentoDomiciliar(!mptSessao.getIndMedicamentoDomiciliar());
		mptSessaoDAO.atualizar(mptSessao);
	}
	
	public void confirmarSuspensaoSessao(PacienteAcolhimentoVO paciente) {
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SSU);			
		mptSessaoDAO.atualizar(mptSessao);
		
		MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
		mptExtratoSessao.setMptSessao(mptSessao);		
		mptExtratoSessao.setIndSituacao(mptSessao.getIndSituacaoSessao());
		mptExtratoSessao.setCriadoEm(new Date());
		mptExtratoSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mptExtratoSessao.setJustificativa(paciente.getMptExtratoSessao().getJustificativa());
		
		if (paciente.getMptExtratoSessao().getMotivo() != null && paciente.getMptExtratoSessao().getMotivo().getSeq() != null){
			mptExtratoSessao.setMotivo(paciente.getMptExtratoSessao().getMotivo());
		}
		
		mptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}

	public void registrarImpressaoPulseira(PacienteAcolhimentoVO paciente) {
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		if (!mptSessao.getIndImpressaoPulseira()){
			mptSessao.setIndImpressaoPulseira(Boolean.TRUE);
			mptSessaoDAO.atualizar(mptSessao);
		}
		
		paciente.setImpressaoPulseira(Boolean.TRUE);
	}
	
	/**
	 * Atualiza a tabela MptSessao e cria MptExtratoSessao.
	 * @param paciente
	 */
	public void concluirAtendimento(Integer seqSessao) {	
		
		//Atualiza a tabela (MPT_SESSAO).
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(seqSessao);
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SAC);			
		mptSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);
	}
	
	/**
	 * Atualiza a tabela MptSessao e cria MptExtratoSessao.
	 * @param parametroSelecionado
	 */
	public void voltarAguardandoAte(ListaPacienteEmAtendimentoVO parametroSelecionado) {	
		
		//Atualiza a tabela (MPT_SESSAO).
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(parametroSelecionado.getSeqSessao());
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SAA);			
		mptSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);
	}

	/**
	 * Atualiza medicamentos administrados em domicilio em MPT_SESSAO.
	 * @param parametroSelecionado
	 */
	public void medicamentoDomiciliar(ListaPacienteEmAtendimentoVO medDomiciliar) {
		//Atualiza a tabela (MPT_SESSAO).
		if (!medDomiciliar.getIndMedicamentoDomiciliar()){
			MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(medDomiciliar.getSeqSessao());
			mptSessao.setIndMedicamentoDomiciliar(DominioSimNao.S.isSim());
			mptSessaoDAO.atualizar(mptSessao);
		}else{
			MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(medDomiciliar.getSeqSessao());
			mptSessao.setIndMedicamentoDomiciliar(DominioSimNao.N.isSim());
			mptSessaoDAO.atualizar(mptSessao);			
		}		
	}
	
	public void voltarParaAtendimento(PacienteConcluidoVO paciente) {	
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SAT);			
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);
	}
	
	public void marcarMedicamentoDomiciliar(PacienteConcluidoVO paciente) {	
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(paciente.getCodigo());
		mptSessao.setIndMedicamentoDomiciliar(!mptSessao.getIndMedicamentoDomiciliar());
		mptSessaoDAO.atualizar(mptSessao);
	}
	
	/**
	 *Identificar registro da MPT_SESSAO com a SEQ_SES do item selecionado  na grid da aba em atendimento #41813 
	 * @param mptSessao
	 */
	public void inserir(MptSessao mptSessao, Date dataInicio) {	
		//Atualiza a tabela (MPT_SESSAO).
		if (mptSessao != null){
			MptSessao original = mptSessaoDAO.obterPorChavePrimaria(mptSessao.getSeq());
			
			Date dataHoraInicio = DateUtil.comporDiaHora(dataInicio, dataInicio);
			Date dataHoraFim = DateUtil.comporDiaHora(new Date(), mptSessao.getDthrFim());
			
			original.setDthrInicio(dataHoraInicio);			
			original.setDthrFim(dataHoraFim);			
			original.setTipoAdministracao(mptSessao.getTipoAdministracao());
			mptSessaoDAO.atualizar(original);			
		}
	}
	
	/**
	 * RN01 e RN02 da estória #41703.
	 * Atualiza a solicitação de dispensação para 'S'.
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void liberarQuimioterapia(Integer codigo, Integer codigoAtendimento, Short seqTipoSessao) throws ApplicationBusinessException{
		//RN01
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(codigo);
		mptSessao.setIndSituacaoAdministracao(DominioSituacaoAdministracao.AAC);
		mptSessaoDAO.atualizar(mptSessao);
		
		String paramIntegracaoCMIV = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_HABILITA_INTEGRACAO_CMIV);
		if(paramIntegracaoCMIV.equals(DominioSituacao.A.toString())){
			List<MptControleDispensacao> listaControles = mptControleDispensacaoDAO.pesquisarControlesDispensacao(codigoAtendimento, 
					mptSessaoDAO.pesquisarSeqSessao(codigo), mptTipoSessaoDAO.pesquisarSeqTipoSessao(seqTipoSessao), 
					parametroFacade.buscarValorShort(AghuParametrosEnum.P_UNF_FARM_CNPQ));
			if(listaControles != null && !listaControles.isEmpty()){
				for (MptControleDispensacao mptControleDispensacao : listaControles) {
					mptControleDispensacao.setIndSolicDispensacao(DominioSimNao.S.isSim());
					mptControleDispensacaoDAO.atualizar(mptControleDispensacao);
				}
			}
		}
		
		//RN02
		MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
		mptExtratoSessao.setMptSessao(mptSessao);		
		mptExtratoSessao.setIndSituacao(DominioSituacaoSessao.AAC);
		mptExtratoSessao.setCriadoEm(new Date());
		mptExtratoSessao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		mptExtratoSessaoDAO.persistir(mptExtratoSessao);
	}

	public void registrarAusenciaPaciente(Integer seqSessao) {

		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(seqSessao);
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SNC);
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);		
	}

	public void voltarStatusAgendado(Integer seqSessao) {
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(seqSessao);
		mptSessao.setIndSituacaoSessao(DominioSituacaoSessao.SSO);
		mptSessaoDAO.atualizar(mptSessao);
		
		inserirTabelaExtratoSessao(mptSessao);		
	}
	/**
	 * ON02 - Verificar se a consulta C1 retorna resultado.
	 * #41703
	 * @param codigoAtendimento
	 * @param seqTipoSessao
	 * @param situacaoSessao
	 * @return Boolean
	 */
	public boolean verificarExisteSessao(Integer seqSessao){
		Integer seq = mptSessaoDAO.pesquisarSeqSessao(seqSessao);
		
		if(seq != null){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
}
