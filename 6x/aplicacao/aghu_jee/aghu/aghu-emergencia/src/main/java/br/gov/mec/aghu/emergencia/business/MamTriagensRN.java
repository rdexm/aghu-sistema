package br.gov.mec.aghu.emergencia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamOrigemPacienteDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamTriagensJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.MamTriagemVO;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamTriagensJn;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamTriagensRN extends BaseBusiness {

	private static final long serialVersionUID = 761590482783879708L;

	@Inject
	private MamTriagensDAO mamTriagemDAO;
	
	@Inject
	private MamOrigemPacienteDAO mamOrigemPacienteDAO;

	@Inject
	private MamTriagensJnDAO mamTriagensJnDAO;

	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Inject
	private AipPacientesDAO pacienteDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO unidadeFuncionalDAO;


	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MamTriagensRNExceptionCode implements BusinessExceptionCode {
		ERRO_SITUACAO_EMERGENCIA_ENC_TRIAGEM_VAZIO
	}
	
	
	public void atualizarTriagemPorTipoMovimento(MamTriagens mamTriagem, MamTriagens mamTriagemOriginal,
			DominioTipoMovimento tipoMovimento, String hostName) throws ApplicationBusinessException {
		
		mamTriagem.setMicNome(hostName);
		mamTriagem.setDthrUltMvto(new Date());
		mamTriagem.setUltTipoMvto(tipoMovimento);
		
		mamTriagem.setServidorUltimoMovimento(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		preUpdateMamTriagens(mamTriagemOriginal, mamTriagem);
		
		if (isMamTriagemAlterada(mamTriagemOriginal, mamTriagem)) {
			
			this.inserirJournalTriagens(mamTriagemOriginal, DominioOperacoesJournal.UPD);
		}
		
		mamTriagem = mamTriagemDAO.merge(mamTriagem);
		
		this.mamTriagemDAO.atualizar(mamTriagem);
	}

	public void atualizarTriagem(MamTriagens mamTriagem, MamTriagens mamTriagemOriginal, Short segSeq, String hostName)
			throws ApplicationBusinessException {
		
		mamTriagem.setMicNome(hostName);
		mamTriagem.setDthrUltSituacao(new Date());
		mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq));
		
		if(mamTriagem.getServidorSituacao() == null){
			mamTriagem.setServidorSituacao(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		}		
		
		
		preUpdateMamTriagens(mamTriagemOriginal, mamTriagem);
		
		if (isMamTriagemAlterada(mamTriagemOriginal, mamTriagem)) {
			
			this.inserirJournalTriagens(mamTriagemOriginal, DominioOperacoesJournal.UPD);
		}
		mamTriagem = mamTriagemDAO.merge(mamTriagem);
		this.mamTriagemDAO.atualizar(mamTriagem);
		this.mamTriagemDAO.flush();
	}
	
	public void atualizarTriagemAcolhimento(MamTriagens triagem, MamTriagens mamTriagemOriginal, MamTriagemVO vo)
			throws ApplicationBusinessException {
		
		triagem.setQueixaPrincipal(vo.getQueixaPrincipal());
		triagem.setInformacoesComplementares(vo.getInformacoesComplementares());
		triagem.setOrigemPaciente(vo.getMamOrigemPaciente());
		triagem.setHouveContato(vo.getHouveContato());
		triagem.setContato(vo.getNomeContato());
		triagem.setDataQueixa(vo.getDataQueixa());
		triagem.setHoraQueixa(vo.getHoraQueixa());
		triagem.setInternado(vo.getIndInternado());
		if (Boolean.TRUE.equals(vo.getIndInternado()) && vo.getSeqHospitalInternado() != null) {
			triagem.setHospitalInternado(this.mamOrigemPacienteDAO.obterPorChavePrimaria(vo.getSeqHospitalInternado()));
		} else {
			triagem.setHospitalInternado(null);
		}
		
		preUpdateMamTriagens(mamTriagemOriginal, triagem);
		
		if (isMamTriagemAlterada(mamTriagemOriginal, triagem)) {
			
			this.inserirJournalTriagens(mamTriagemOriginal, DominioOperacoesJournal.UPD);
		}
		this.mamTriagemDAO.atualizar(triagem);
	}
	
	private boolean isMamTriagemAlterada(MamTriagens mamTriagemOriginal, MamTriagens mamTriagem) {
		
		mamTriagem = mamTriagemDAO.merge(mamTriagem);
		
		return CoreUtil.modificados(mamTriagem.getQueixaPrincipal(), mamTriagemOriginal.getQueixaPrincipal()) ||
				CoreUtil.modificados(mamTriagem.getInformacoesComplementares(), mamTriagemOriginal.getInformacoesComplementares()) ||	
				CoreUtil.modificados(mamTriagem.getPaciente() , mamTriagemOriginal.getPaciente()) ||	
				CoreUtil.modificados(mamTriagem.getUnidadeFuncional(), mamTriagemOriginal.getUnidadeFuncional()) ||
				CoreUtil.modificados(mamTriagem.getCriadoEm(), mamTriagemOriginal.getCriadoEm()) ||
				CoreUtil.modificados(mamTriagem.getUltTipoMvto(), mamTriagemOriginal.getUltTipoMvto()) ||
				CoreUtil.modificados(mamTriagem.getIndPacAtendimento(), mamTriagemOriginal.getIndPacAtendimento()) ||
				CoreUtil.modificados(mamTriagem.getIndPacEmergencia(), mamTriagemOriginal.getIndPacEmergencia()) ||
				CoreUtil.modificados(mamTriagem.getDthrInicio(), mamTriagemOriginal.getDthrInicio()) ||
				CoreUtil.modificados(mamTriagem.getDthrFim(), mamTriagemOriginal.getDthrFim()) ||
				CoreUtil.modificados(mamTriagem.getDthrUltMvto(), mamTriagemOriginal.getDthrUltMvto()) ||
				CoreUtil.modificados(mamTriagem.getDthrUltSituacao(), mamTriagemOriginal.getDthrUltSituacao()) ||
				CoreUtil.modificados(mamTriagem.getMicNome(), mamTriagemOriginal.getMicNome()) ||
				CoreUtil.modificados(mamTriagem.getServidor(), mamTriagemOriginal.getServidor()) ||
				CoreUtil.modificados(mamTriagem.getServidorSituacao(), mamTriagemOriginal.getServidorSituacao()) ||
				CoreUtil.modificados(mamTriagem.getServidorUltimoMovimento(), mamTriagemOriginal.getServidorUltimoMovimento()) ||
				CoreUtil.modificados(mamTriagem.getSituacaoEmergencia(), mamTriagemOriginal.getSituacaoEmergencia());
	}

	/***
	 * @ORADB MAMT_TRG_ARU - Executar depois de update na tabela MAM_TRIAGENS
	 * @param mamTriagensOriginal
	 * @param operacao
	 */	
	public void inserirJournalTriagens(MamTriagens mamTriagensOriginal, DominioOperacoesJournal operacao) {

		MamTriagensJn mamTriagensJn = new MamTriagensJn();

		mamTriagensJn.setNomeUsuario(usuario.getLogin());
		mamTriagensJn.setOperacao(operacao);
		mamTriagensJn.setSerVinCodigoSituacao(mamTriagensOriginal.getServidorSituacao().getId().getVinCodigo());
		mamTriagensJn.setSerMatriculaSituacao(mamTriagensOriginal.getServidorSituacao().getId().getMatricula());
		mamTriagensJn.setDthrUltMvto(mamTriagensOriginal.getDthrUltMvto());
		mamTriagensJn.setMicNome(mamTriagensOriginal.getMicNome());
		if (mamTriagensOriginal.getSituacaoEmergencia() != null) {
			MamSituacaoEmergencia situacaoEmergencia = this.mamSituacaoEmergenciaDAO
					.obterPorChavePrimaria(mamTriagensOriginal
							.getSituacaoEmergencia().getSeq());
			mamTriagensJn.setSituacaoEmergencia(situacaoEmergencia);
		}

		mamTriagensJn.setSerVinCodigo(mamTriagensOriginal.getServidor().getId().getVinCodigo());
		mamTriagensJn.setSerMatricula(mamTriagensOriginal.getServidor().getId().getMatricula());
		mamTriagensJn.setSerVinCodigoUltimoMovimento(mamTriagensOriginal.getServidorUltimoMovimento().getId().getVinCodigo());
		mamTriagensJn.setSerMatriculaUltimoMovimento(mamTriagensOriginal.getServidorUltimoMovimento().getId().getMatricula());
		mamTriagensJn.setUltTipoMvto(mamTriagensOriginal.getUltTipoMvto());
		mamTriagensJn.setDthrUltSituacao(mamTriagensOriginal.getDthrUltSituacao());
		mamTriagensJn.setSeq(mamTriagensOriginal.getSeq());
		mamTriagensJn.setQueixaPrincipal(mamTriagensOriginal.getQueixaPrincipal());
		mamTriagensJn.setInformacoesComplementares(mamTriagensOriginal.getInformacoesComplementares());
		mamTriagensJn.setPacCodigo(mamTriagensOriginal.getPaciente().getCodigo());
		mamTriagensJn.setUnfSeq(mamTriagensOriginal.getUnidadeFuncional().getSeq());
		mamTriagensJn.setCriadoEm(mamTriagensOriginal.getCriadoEm());
		mamTriagensJn.setDthrInicio(mamTriagensOriginal.getDthrInicio());
		mamTriagensJn.setDthrFim(mamTriagensOriginal.getDthrFim());
		mamTriagensJn.setIndPacAtendimento(mamTriagensOriginal.getIndPacAtendimento());
		mamTriagensJn.setIndPacEmergencia(mamTriagensOriginal.getIndPacEmergencia());
		//Setado valor default.
		mamTriagensJn.setInternado(Boolean.FALSE);
		
		if (mamTriagensOriginal.getHospitalInternado() != null) {
			MamOrigemPaciente origemPaciente = this.mamOrigemPacienteDAO
					.obterPorChavePrimaria(mamTriagensOriginal
							.getHospitalInternado().getSeq());
			mamTriagensJn.setHospitalInternado(origemPaciente);
		}

		this.mamTriagensJnDAO.persistir(mamTriagensJn);
	}

	public MamTriagens inserirTriagem(Paciente paciente, Short seqUnidadeDestino, String nomeComputador, MamUnidAtendem unidade, String nomeResponsavel) throws ApplicationBusinessException {
		MamTriagens triagem = preInserirMamTriagens(nomeComputador);
		triagem.setQueixaPrincipal("Não Informado");		
		triagem.setPaciente(pacienteDAO.obterPorChavePrimaria(paciente.getCodigo()));		
		triagem.setUnidadeFuncional(unidadeFuncionalDAO.obterPorChavePrimaria(seqUnidadeDestino));
		triagem.setDthrInicio(new Date());
		triagem.setDthrUltMvto(new Date());
		triagem.setNomeResponsavelConta(nomeResponsavel);
		List<Short> segSeqs = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_TRIAGEM);
		if(segSeqs == null || segSeqs.isEmpty()) {
			throw new ApplicationBusinessException(MamTriagensRNExceptionCode.ERRO_SITUACAO_EMERGENCIA_ENC_TRIAGEM_VAZIO);
		}
		triagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeqs.get(0)));
		triagem.setUltTipoMvto(DominioTipoMovimento.E);
		triagem.setDthrUltSituacao(new Date());
		
		triagem.setUnidadeAtendimento(unidade);
		
		this.mamTriagemDAO.persistir(triagem);
		this.mamTriagemDAO.flush();
		
		return triagem;
	}

	/**
	 * @ORADB MAMT_TRG_BRI - Executar antes de inserir na tabela MAM_TRIAGENS
	 * @param nomeComputador
	 * @return triagem
	 */
	public MamTriagens preInserirMamTriagens(String nomeComputador) {
		MamTriagens triagem = new MamTriagens();
		triagem.setCriadoEm(new Date());
		triagem.setIndPacAtendimento(DominioPacAtendimento.S);
		triagem.setIndPacEmergencia(Boolean.TRUE);		
		triagem.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));		
		triagem.setServidorUltimoMovimento(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));		
		triagem.setServidorSituacao(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));		
		triagem.setMicNome(nomeComputador);
		
		return triagem;
	}
	
	
	/**
	 * @ORADB MAMT_TRG_BRU - Executar antes de atualizar na tabela MAM_TRIAGENS
	 * @param triagemOriginal, Triagem
	 * @return triagem
	 */
	public void preUpdateMamTriagens(MamTriagens mamTriagemOriginal, MamTriagens mamTriagem) throws ApplicationBusinessException {
		
		mamTriagem = mamTriagemDAO.merge(mamTriagem);
		
		if (CoreUtil.modificados(mamTriagem.getSituacaoEmergencia().getSeq(), mamTriagemOriginal.getSituacaoEmergencia().getSeq())){
			this.objetosOracleDAO.faturamentoCheckOut(mamTriagemOriginal
					.getSituacaoEmergencia().getSeq().intValue(), mamTriagem
					.getSituacaoEmergencia().getSeq().intValue(), mamTriagem
					.getSeq().intValue(), mamTriagem.getDthrUltMvto(), usuario.getLogin());
		}
		
		if (mamTriagem.getUltTipoMvto().equals(DominioTipoMovimento.C)) {
			
			this.objetosOracleDAO.limparResumoCasoPaciente(mamTriagem.getSeq().longValue(), usuario.getLogin());			
			this.objetosOracleDAO.limparPlanoAcaoPaciente(mamTriagem.getSeq().longValue(), usuario.getLogin());
			this.objetosOracleDAO.retirarPacienteListas(mamTriagem.getSeq().longValue(), usuario.getLogin());
			this.objetosOracleDAO.limpaInformacoesPaciente(mamTriagem.getSeq().longValue(), usuario.getLogin());
			
		}
		
	}
	
}
