package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfCirurgiaJn;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável pelas regras de BANCO para MBC_PROF_CIRURGIAS
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcProfCirurgiasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcProfCirurgiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProfCirurgiaJnDAO mbcProfCirurgiaJnDAO;

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5589764428856091651L;

	public enum MbcProfCirurgiasRNExceptionCode implements BusinessExceptionCode {
		MBC_00336, MBC_00337, MBC_00326, MBC_00327, MBC_00341, MBC_00338, MBC_00339, MBC_00453, MBC_00332, MBC_00334,
		ERRO_PROFISSIONAL_REALIZOU_CIRURGIA;
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcProfCirurgias
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public MbcProfCirurgias persistirProfissionalCirurgias(MbcProfCirurgias profCirurgias) throws BaseException {
		MbcProfCirurgias retornoProfCirurgias = profCirurgias;
		if(profCirurgias.getVersion() == null){ //Insere
			this.inserirMbcProfCirurgias(retornoProfCirurgias);
		}else { // Atualizar
			retornoProfCirurgias = this.atualizarMbcProfCirurgias(retornoProfCirurgias);
		}
		return retornoProfCirurgias;
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER MBCT_PCG_BRI (INSERT)
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @param veioGeraEscalaD
	 * @throws BaseException
	 */
	public void preInserirMbcProfCirurgias(MbcProfCirurgias profCirurgias, final boolean veioGeraEscalaD, final boolean veioCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		/**
		 * em 08/08/2013 mutanting e valores invalidos - regra retirada por Liziane por não existir auto_relacionamento valido na tabela PUC
		 * this.atualizarVinculoConvenioSaude(profCirurgias); // RN1
		 */
		
		this.verificarFuncaoProfissionalCirurgia(profCirurgias); // RN2
		if (Boolean.FALSE.equals(veioCirurgia)) {
			this.verificarGrupoConvenioBlocoCirurgico(profCirurgias);
		}
		this.verificarProfissionalResponsavelRealizouCirurgia(profCirurgias); // RN3
		// Verifica se a variável de package MBCK_CEC_RN.V_VEIO_GERA_ESCALA_D é VERDADEIRA
		if (Boolean.FALSE.equals(veioGeraEscalaD)) {
			this.verificarUnidadeFuncionalProfissionalCirurgia(profCirurgias); // RN4
		}
		profCirurgias.setCriadoEm(new Date()); // RN5: Atualiza com a DATA ATUAL
		profCirurgias.setServidor(servidorLogado); // RN6: Atualiza com SERVIDOR LOGADO
		
		this.verificarProfissionalResponsavel(profCirurgias);
	}
	
	private void verificarProfissionalResponsavel(MbcProfCirurgias profCirurgias) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MbcCirurgias cirurgia =  getMbcCirurgiasDAO().obterPorChavePrimaria(profCirurgias.getId().getCrgSeq());
		
		if(cirurgia.getAtendimento() != null && profCirurgias.getIndResponsavel()
			&& DominioPacAtendimento.S.equals(cirurgia.getAtendimento().getIndPacAtendimento())
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.RZDA)
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {			
			try {
				getMbcCirurgiasDAO().flush();
				getSolicitacaoExameFacade().gerarExameProvaCruzadaTransfusional(
						cirurgia.getAtendimento(), cirurgia, null, 
						profCirurgias.getServidorPuc(), Boolean.TRUE);
			} catch(InactiveModuleException e) {
				logWarn(e.getMessage());
				this.getObjetosOracleDAO()
						.gerarExameProvaCruzadaTransfusional(
								cirurgia.getAtendimento().getSeq(), cirurgia.getSeq(), servidorLogado, profCirurgias.getServidorPuc(), DominioSimNao.S.toString());
			}
		}
	}

	private ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	/**
	 * Inserir MbcProfCirurgias com escala
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @param veioGeraEscalaD
	 * @throws BaseException
	 */
	public void inserirMbcProfCirurgias(MbcProfCirurgias profCirurgias, boolean veioGeraEscalaD, boolean veioCirurgia) throws BaseException {
		this.preInserirMbcProfCirurgias(profCirurgias, veioGeraEscalaD, veioCirurgia);
		this.getMbcProfCirurgiasDAO().persistir(profCirurgias);
	}

	/**
	 * Inserir MbcProfCirurgias
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcProfCirurgias(MbcProfCirurgias profCirurgias) throws BaseException {
		this.preInserirMbcProfCirurgias(profCirurgias, Boolean.FALSE, Boolean.FALSE);
		this.getMbcProfCirurgiasDAO().persistir(profCirurgias);
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_PCG_BRU (UPDATE)
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public MbcProfCirurgias preAtualizarMbcProfCirurgias(MbcProfCirurgias profCirurgias, Boolean atualizarCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		MbcProfCirurgias retornoProfCirurgias = profCirurgias;
		this.verificarFuncaoProfissionalCirurgia(retornoProfCirurgias); // RN1
		this.verificarGrupoConvenioBlocoCirurgico(retornoProfCirurgias); // RN2
		this.verificarProfissionalResponsavelRealizouCirurgia(retornoProfCirurgias); // RN3
		this.verificarUnidadeFuncionalProfissionalCirurgia(retornoProfCirurgias); // RN4
		
		//retornoProfCirurgias = this.atualizarCentroCustoESala(retornoProfCirurgias, atualizarCirurgia);
		
		retornoProfCirurgias = this.atualizarCentroCusto(retornoProfCirurgias, atualizarCirurgia); // RN5
		retornoProfCirurgias = this.atualizarUtilizacaoSala(retornoProfCirurgias); // RN6

		// Seta o servidor responsável pela alteração do registro
		retornoProfCirurgias.setServidor(servidorLogado);

		return retornoProfCirurgias;
	}
	
	public MbcProfCirurgias atualizarMbcProfCirurgias(MbcProfCirurgias profCirurgias) throws BaseException {
		return atualizarMbcProfCirurgias(profCirurgias, true);
	}

	/**
	 * Atualizar MbcProfCirurgias
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public MbcProfCirurgias atualizarMbcProfCirurgias(MbcProfCirurgias profCirurgias, Boolean atualizarCirurgia) throws BaseException {
		MbcProfCirurgias retornoProfCirurgias = this.preAtualizarMbcProfCirurgias(profCirurgias, atualizarCirurgia);
		retornoProfCirurgias = this.getMbcProfCirurgiasDAO().merge(retornoProfCirurgias);
		this.getMbcProfCirurgiasDAO().flush();
		this.posAtualizarMbcProfCirurgias(retornoProfCirurgias);
		return retornoProfCirurgias;
	}

	/**
	 * ORADB TRIGGER MBCT_PCG_ARU (UPDATE)
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public void posAtualizarMbcProfCirurgias(MbcProfCirurgias profCirurgias) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// Obtem o registro antigo
		MbcProfCirurgias original = getMbcProfCirurgiasDAO().obterOriginal(profCirurgias);

		// Verifica se o registro foi modificado
		final boolean isModificado = CoreUtil.modificados(profCirurgias.getId().getCrgSeq(), original.getId().getCrgSeq())
		|| CoreUtil.modificados(profCirurgias.getMbcProfAtuaUnidCirgs(), original.getMbcProfAtuaUnidCirgs())
		|| CoreUtil.modificados(profCirurgias.getIndResponsavel(), original.getIndResponsavel())
		|| CoreUtil.modificados(profCirurgias.getIndRealizou(), original.getIndRealizou())
		|| CoreUtil.modificados(profCirurgias.getIndInclEscala(), original.getIndInclEscala()) || CoreUtil.modificados(profCirurgias.getServidor(), original.getServidor())
		|| CoreUtil.modificados(profCirurgias.getMbcProfAtuaUnidCirgsVinc(), original.getMbcProfAtuaUnidCirgsVinc());

		if (isModificado) { // Quando modificado grava na journal

			// Instancia novo registro na journal
			MbcProfCirurgiaJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MbcProfCirurgiaJn.class, servidorLogado.getUsuario());

			jn.setCrgSeq(profCirurgias.getId().getCrgSeq());

			// Profissionais que atuam na unidade cirurgica
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs = profCirurgias.getMbcProfAtuaUnidCirgs();
			if (profAtuaUnidCirgs != null) {
				jn.setPucSerMatricula(profAtuaUnidCirgs.getId().getSerMatricula());
				jn.setPucSerVinCodigo(profAtuaUnidCirgs.getId().getSerVinCodigo());
				jn.setPucUnfSeq(profAtuaUnidCirgs.getId().getUnfSeq());
				jn.setPucIndFuncaoProf(profAtuaUnidCirgs.getId().getIndFuncaoProf());
			}

			jn.setIndResponsavel(profCirurgias.getIndResponsavel());
			jn.setIndRealizou(profCirurgias.getIndRealizou());
			jn.setIndInclEscala(profCirurgias.getIndInclEscala());
			jn.setCriadoEm(profCirurgias.getCriadoEm());

			// Servidor responsável
			RapServidores servidor = profCirurgias.getServidor();
			if (servidor != null) {
				jn.setSerMatricula(servidor.getId().getMatricula());
				jn.setSerVinCodigo(servidor.getId().getVinCodigo());
			}

			// Profissionais VINCULADOS que atuam na unidade cirurgica
			MbcProfAtuaUnidCirgs profAtuaUnidCirgsVinc = profCirurgias.getMbcProfAtuaUnidCirgsVinc();
			if (profAtuaUnidCirgsVinc != null) {
				jn.setPucSerMatriculaVinc(profAtuaUnidCirgsVinc.getId().getSerMatricula());
				jn.setPucSerVinCodigoVinc(profAtuaUnidCirgsVinc.getId().getSerVinCodigo());
				jn.setPucUnfSeqVinc(profAtuaUnidCirgsVinc.getId().getUnfSeq());
				jn.setPucIndFuncaoProfVinc(profAtuaUnidCirgsVinc.getId().getIndFuncaoProf());
			}

			// Insere na JOURNAL
			this.getMbcProfCirurgiaJnDAO().persistir(jn);

		}

	}

	/*
	 * Métodos REMOVER
	 */

	/**
	 * ORADB TRIGGER MBCT_PCG_BRD (DELETE)
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @param veioGeraEscalaD
	 * @throws BaseException
	 */
	public void preRemoverMbcProfCirurgias(MbcProfCirurgias profCirurgias, boolean veioGeraEscalaD) throws BaseException {
		// Verifica se a variável de package MBCK_CEC_RN.V_VEIO_GERA_ESCALA_D é FALSA
		if (Boolean.FALSE.equals(veioGeraEscalaD)) {
			/*
			 * Verifica se a natureza do agendamento for eletiva e o usuário não tem perfil de 'agendar cirurgia não prevista' e já foi executada a escala definitiva, não
			 * permitindo mais atualizar o profissional da cirurgia.
			 */
			this.verificarNaturezaAgendamentoEletiva(profCirurgias.getId().getCrgSeq());
		}
	}

	/**
	 * Remover MbcProfCirurgias
	 * 
	 * @param profCirurgias
	 * @param servidorLogado
	 * @param veioGeraEscalaD
	 * @throws BaseException
	 */
	public void removerMbcProfCirurgias(MbcProfCirurgias profCirurgias, boolean veioGeraEscalaD) throws BaseException {
		if(profCirurgias != null){
			this.preRemoverMbcProfCirurgias(profCirurgias, veioGeraEscalaD);
			this.getMbcProfCirurgiasDAO().remover(profCirurgias);
		}	
	}

	/*
	 * PROCEDURES
	 */

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_ATU_VINCULO
	 * <p>
	 * Atualiza vínculo através do convênio de saúde
	 * </p>
	 * 
	 * @param profCirurgias
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void atualizarVinculoConvenioSaude(MbcProfCirurgias profCirurgias) throws BaseException {

		// Verifica se há indicação de responsável
		if (profCirurgias.getIndResponsavel()) {

			// Pesquisa MbcProfAtuaUnidCirgs filho do MbcProfAtuaUnidCirgs de profCirurgias. Obs. Migração do CURSOR C_PUC
			final MbcProfAtuaUnidCirgs paiProfAtuaUnidCirgs = profCirurgias.getMbcProfAtuaUnidCirgs();
			final MbcProfAtuaUnidCirgs filhoProfAtuaUnidCirgs = paiProfAtuaUnidCirgs.getMbcProfAtuaUnidCirgs();

			// Obtem o grupo do convênio
			DominioGrupoConvenio valorGrupoConvenio = profCirurgias.getCirurgia().getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();

			if (valorGrupoConvenio == null) {
				// Convênio de saúde não cadastrado
				throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00336);
			} else {

				final Boolean isUnidadeFuncionalBloco = getAghuFacade().verificarCaracteristicaUnidadeFuncional(profCirurgias.getUnidadeFuncional().getSeq(),
						ConstanteAghCaractUnidFuncionais.BLOCO);

				// Obtem o servidor
				RapServidores servidorFilhoProfAtuaUnidCirgs = filhoProfAtuaUnidCirgs != null ? filhoProfAtuaUnidCirgs.getRapServidores() : null;

				/*
				 * Verifica se a unidade funcional é BLOCO. O grupo do convênio é 'S'. A função do profissional é MÉDICO PROFESSOR. O VÍNCULO do servidor PUC é maior que ZERO
				 */
				if (isUnidadeFuncionalBloco
						&& DominioGrupoConvenio.S.equals(valorGrupoConvenio)
						&& !DominioFuncaoProfissional.MPF.equals(profCirurgias.getFuncaoProfissional())
						&& (servidorFilhoProfAtuaUnidCirgs != null && servidorFilhoProfAtuaUnidCirgs.getId() != null
								&& servidorFilhoProfAtuaUnidCirgs.getId().getVinCodigo() != null && servidorFilhoProfAtuaUnidCirgs.getId().getVinCodigo() > 0)) {
					/*
					 * Seta em PUC
					 */
					profCirurgias.setMbcProfAtuaUnidCirgs(filhoProfAtuaUnidCirgs);

					/*
					 * Seta em PUC VINC
					 */
					profCirurgias.setMbcProfAtuaUnidCirgsVinc(paiProfAtuaUnidCirgs);

				}

			}

		}
	}

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_FUNCAO
	 * <p>
	 * Vericar se a função do profissional informada existe
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public void verificarFuncaoProfissionalCirurgia(MbcProfCirurgias profCirurgias) throws BaseException {

		MbcProfAtuaUnidCirgs profAtuaUnidCirgs = getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(new MbcProfAtuaUnidCirgsId(profCirurgias.getId().getPucSerMatricula(), profCirurgias.getId().getPucSerVinCodigo(), profCirurgias.getId().getPucUnfSeq(), profCirurgias.getId().getPucIndFuncaoProf()) );

		if (profAtuaUnidCirgs == null) {
			// Servidor não cadastrado para a unidade e função
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00326);
		} else if (DominioSituacao.I.equals(profAtuaUnidCirgs.getSituacao())) {
			// Servidor está inativo para a unidade e função
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00327);
		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_CRG_BLOC
	 * <p>
	 * Vericar o grupo grupo convênio do bloco cirurgico
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public void verificarGrupoConvenioBlocoCirurgico(MbcProfCirurgias profCirurgias) throws BaseException {

		// Quando há indicação de responsável
		if (profCirurgias.getIndResponsavel()) {
			// obter cirurgia atraves do CrgSeq
			MbcCirurgias cirurgia =  getMbcCirurgiasDAO().obterPorChavePrimaria(profCirurgias.getId().getCrgSeq());
			// Obtem o grupo do convênio
			DominioGrupoConvenio valorGrupoConvenio = cirurgia.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();
			
			if (valorGrupoConvenio == null) {
				// Convênio de saúde não cadastrado
				throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00336);
			} else {

				final Boolean isUnidadeFuncionalBloco = getAghuFacade().verificarCaracteristicaUnidadeFuncional(profCirurgias.getId().getPucUnfSeq(),
						ConstanteAghCaractUnidFuncionais.BLOCO);

				if (this.isHCPA() && isUnidadeFuncionalBloco && DominioGrupoConvenio.S.equals(valorGrupoConvenio) && !DominioFuncaoProfissional.MPF.equals(profCirurgias.getFuncaoProfissional())) {
					// Profissional responsável por cirurgia SUS no Bloco deverá ser professor
					throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00337);
				}

			}

		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_REALIZOU
	 * <p>
	 * Verifica a função do responsável pela cirurgia quando realizada. Se realizou a ciurgia somente os seguintes profissionais serão aceitos: Médico contratado, Médico professor
	 * e Médico auxiliar
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public void verificarProfissionalResponsavelRealizouCirurgia(MbcProfCirurgias profCirurgias) throws BaseException {

		if(profCirurgias.getIndRealizou() == null){
			profCirurgias.setIndRealizou(Boolean.FALSE);
		}
		
		// Se realizou a ciurgia somente os seguintes profissionais serão aceitos: Médico contratado, Médico professor e Médico auxiliar
		if (Boolean.TRUE.equals(profCirurgias.getIndRealizou())) {

			// Obtem a função do profissional
			final DominioFuncaoProfissional funcaoProfissional = profCirurgias.getFuncaoProfissional();

			// Verfica se a função do profissional permite a realização da cirurgia
			boolean isFuncoesProfissionalPermitidas = DominioFuncaoProfissional.MCO.equals(funcaoProfissional) || DominioFuncaoProfissional.MPF.equals(funcaoProfissional)
			|| DominioFuncaoProfissional.MAX.equals(funcaoProfissional);

			if (!isFuncoesProfissionalPermitidas) {
				// Profissional responsável pela realização da cirurgia deve ser médico
				throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.ERRO_PROFISSIONAL_REALIZOU_CIRURGIA);
			}

		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_UNID_CIR
	 * <p>
	 * Garante que a unidade do profissional da cirurgia seja a mesma da cirurgia
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public void verificarUnidadeFuncionalProfissionalCirurgia(MbcProfCirurgias profCirurgias) throws BaseException {

		// Obtem a cirurgia
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(profCirurgias.getId().getCrgSeq());
		AghUnidadesFuncionais unid = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(profCirurgias.getId().getPucUnfSeq());
		if (cirurgia == null) {
			// Cirurgia não cadastrada
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00338);
		} else if (!unid.equals(cirurgia.getUnidadeFuncional())) {
			// Unidade do Profissional deve ser a mesma da cirurgia
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00339);
		}

	}
	
	public MbcProfCirurgias atualizarCentroCustoESala(MbcProfCirurgias profCirurgias, Boolean atualizarCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcProfCirurgias retornoProfCirurgias = profCirurgias;
		// Quando há indicação de responsável
		if (retornoProfCirurgias.getIndResponsavel()) {

			// Obtem a cirurgia
			MbcCirurgias cirurgia = retornoProfCirurgias.getCirurgia();
			RapServidores servidor = retornoProfCirurgias.getServidorPuc();
			Date valorData = null;
			
			if (cirurgia != null) {
				// Obtem os valores necessários para processar a utilização da sala
				valorData = cirurgia.getDataInicioCirurgia();
				final MbcSalaCirurgica valorSalaUnidade = cirurgia.getSalaCirurgica(); // v_sala e v_unidade
				final DominioNaturezaFichaAnestesia valorNatureza = cirurgia.getNaturezaAgenda();

				if (valorData != null) {

					/*
					 * Chamada da FUNCTION MBCC_UTILIZACAO_SALA Obtem o valor de utilização que será atualizado na cirurgia
					 */
					DominioUtilizacaoSala valorUtilizacao = this.verificarUtilizacaoSala(valorData, valorSalaUnidade.getId().getSeqp(), valorSalaUnidade.getId().getUnfSeq(),
							retornoProfCirurgias.getServidorPuc().getId().getMatricula(), retornoProfCirurgias.getServidorPuc().getId().getVinCodigo(), valorNatureza);

					// Seta valor de utilização da sala na cirurgia
					cirurgia.setUtilizacaoSala(valorUtilizacao);
				}
			}
			
			boolean salvaSala = cirurgia != null && valorData != null;
			boolean salvaCentroCusto = (servidor != null && servidor.getCentroCustoLotacao() != null) && atualizarCirurgia;

			if(salvaCentroCusto || salvaSala){
				// ATUALIZA a cirurgia com o alor de utilização
				cirurgia = this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, null, servidorLogado.getDtFimVinculo());
			}
			
			if (salvaCentroCusto) {
				retornoProfCirurgias.setCirurgia(cirurgia);
				// Seta o centro de custo na cirurgia do profissional responsável
				cirurgia.setCentroCustos(servidor.getCentroCustoLotacao());
			}
			
			if (salvaSala) {
				retornoProfCirurgias.setCirurgia(cirurgia);
			}

		}
		return retornoProfCirurgias;
	}	

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_ATU_CCUSTO
	 * <p>
	 * Atualizar centro de custo da cirurgia com o centro de custo onde está alocado o cirurgião responsável
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public MbcProfCirurgias atualizarCentroCusto(MbcProfCirurgias profCirurgias, Boolean atualizarCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcProfCirurgias retornoProfCirurgias = profCirurgias;
		// Quando há indicação de responsável
		if (retornoProfCirurgias.getIndResponsavel()) {

			RapServidores servidor = retornoProfCirurgias.getServidorPuc();

			if (servidor != null && servidor.getCentroCustoLotacao() != null) {
				if (atualizarCirurgia) {
					// Obtem a cirurgia do profissional responsável
					MbcCirurgias cirurgia = retornoProfCirurgias.getCirurgia();
					// ATUALIZA a cirurgia com o novo centro de custo
					cirurgia = this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, null, servidorLogado.getDtFimVinculo());
					retornoProfCirurgias.setCirurgia(cirurgia);
					// Seta o centro de custo na cirurgia do profissional responsável
					cirurgia.setCentroCustos(servidor.getCentroCustoLotacao());
				}
			}

		}
		return retornoProfCirurgias;
	}

	/**
	 * ORADB PROCEDURE RN_PCGP_ATU_UTIL_SL
	 * <p>
	 * Atualizar utilizacao de sala de acordo com a reserva da sala para o profissional responsável
	 * </p>
	 * 
	 * @param profCirurgias
	 * @throws BaseException
	 */
	public MbcProfCirurgias atualizarUtilizacaoSala(MbcProfCirurgias profCirurgias) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcProfCirurgias retornoProfCirurgias = profCirurgias;
		// Quando há indicação de responsável
		if (retornoProfCirurgias.getIndResponsavel()) {

			// Obtem a cirurgia
			MbcCirurgias cirurgia = retornoProfCirurgias.getCirurgia();

			if (cirurgia != null) {

				// Obtem os valores necessários para processar a utilização da sala
				final Date valorData = cirurgia.getDataInicioCirurgia();
				final MbcSalaCirurgica valorSalaUnidade = cirurgia.getSalaCirurgica(); // v_sala e v_unidade
				final DominioNaturezaFichaAnestesia valorNatureza = cirurgia.getNaturezaAgenda();

				if (valorData != null) {

					/*
					 * Chamada da FUNCTION MBCC_UTILIZACAO_SALA Obtem o valor de utilização que será atualizado na cirurgia
					 */
					DominioUtilizacaoSala valorUtilizacao = this.verificarUtilizacaoSala(valorData, valorSalaUnidade.getId().getSeqp(), valorSalaUnidade.getId().getUnfSeq(),
							retornoProfCirurgias.getServidorPuc().getId().getMatricula(), retornoProfCirurgias.getServidorPuc().getId().getVinCodigo(), valorNatureza);

					// Seta valor de utilização da sala na cirurgia
					cirurgia.setUtilizacaoSala(valorUtilizacao);

					// ATUALIZA a cirurgia com o alor de utilização
					cirurgia = this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, null, servidorLogado.getDtFimVinculo());
					retornoProfCirurgias.setCirurgia(cirurgia);
				}

			}

		}
		return retornoProfCirurgias;
	}

	/**
	 * ORADB PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_ALT_ELET
	 * <p>
	 * Verifica a natureza do agendamento eletiva
	 * </p>
	 * 
	 * @param crgSeq
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void verificarNaturezaAgendamentoEletiva(final Integer crgSeq) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// Obtem a cirurgia
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);

		// Verifica se a natureza da cirurgia é ELETIVA
		if (cirurgia != null && DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {

			// Verificar se o usuário tem a permissão: AGENDAR CIRURGIA NAO PREVISTA
			final boolean isPerfilAgendarCirurgiaNaoPrevista = this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "agendarCirurgiaNaoPrevista");

			// Verifica se o usuário NÃO TEM o perfil para AGENDAR CIRURGIA NAO PREVISTA
			if (!isPerfilAgendarCirurgiaNaoPrevista) {

				boolean isControleEscalaCirurgica = getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(cirurgia.getUnidadeFuncional().getSeq(),
						cirurgia.getData(), DominioTipoEscala.D);

				// Verifica a existência de prévia de ESCALA DEFINITIVA
				if (isControleEscalaCirurgica) {
					// Já foi executada a escala definitiva para esta data
					throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00453);
				}

			}

		}

	}

	/*
	 * FUNCTIONS
	 */

	/**
	 * ORADB FUNCTION MBCC_UTILIZACAO_SALA
	 * 
	 * @param dataCirurgia
	 * @param sala
	 * @param unidadeSala
	 * @param matriculaResp
	 * @param vinCodigoResp
	 * @param natureza
	 * @return
	 */
	public DominioUtilizacaoSala verificarUtilizacaoSala(Date dataCirurgia, Short sala, Short unidadeSala, Integer matriculaResp, Short vinCodigoResp,
			DominioNaturezaFichaAnestesia natureza) {

		/*
		 * Descrição IMPORTANTE do AGH:
		 * 
		 * "Função que recebe:
		 * 
		 * Data da cirurgia Horário de início Sala Empregador/servidor da equipe responsável Caráter da cirurgia
		 * 
		 * Função retorna:
		 * 
		 * PRE - sala prevista NPR - sala não prevista
		 * 
		 * Se o caráter da cirurgia for Emergência: retorna PRE (sempre a sala vai ser prevista pras emergencias)
		 * 
		 * Se o caráter for Urgência: Se a sala estiver reservada pra urgência, Retorna PRE (sala prevista para urgências) Se não, Segue a regra do caráter Eletiva
		 * 
		 * Se o caráter for eletiva: A partir da data da cirurgia, buscar o dia da semana. A partir do horário de início, buscar o turno da cirurgia (pode usar como exemplo a regra
		 * RN_CRGP_VER_CARAC_SA) .
		 * 
		 * Acessar a tab MBC_CARACT_SALA_ESPS buscando a(s) equipe(s) para a qual aquela sala/dia semana/turno está reservada. Se o profissional para o qual está sendo agendada a
		 * cirurgia = profissional para o qual a sala está reservada, então Retorna PRE Se não Verificar se a sala não foi cedida pra outro profissional, pra isto acessar a tab
		 * MBC_SUBST_ESCALA_SALA pela data da cirurgia e turno, verificando se foi cedida pra outro profissional (puc_ser_matricula, puc_ser_vin_codigo Se profissional para o qual
		 * está sendo agendada a cirurgia = profissional para o qual a sala foi cedida, então Retorna PRE Se não Retorna NPR Obs: Se a sala/dia semana/ turno não estiver reservada
		 * pra nenhum profissional e nem cedida pra ninguém...retorna NPR"
		 */

		if (DominioNaturezaFichaAnestesia.EMG.equals(natureza)) {
			return DominioUtilizacaoSala.PRE;
		}

		if (DominioNaturezaFichaAnestesia.URG.equals(natureza)) {

			// Obtem a urgência da característica da sala cirurgica através da sala
			boolean isUrgente = this.getMbcCaracteristicaSalaCirgDAO().obterUrgenciaCaracteristicaSalaCirgPorSalaCirurgica(sala, unidadeSala);
			if (isUrgente) {
				return DominioUtilizacaoSala.PRE;
			}

			return this.verificarUtilizacaoSalaDominioUtilizacaoSala(dataCirurgia, sala, unidadeSala, matriculaResp, vinCodigoResp, natureza);

		}

		if (DominioNaturezaFichaAnestesia.ELE.equals(natureza) || DominioNaturezaFichaAnestesia.APR.equals(natureza) || DominioNaturezaFichaAnestesia.ESP.equals(natureza)) {
			// Eletiva
			return this.verificarUtilizacaoSalaDominioUtilizacaoSala(dataCirurgia, sala, unidadeSala, matriculaResp, vinCodigoResp, natureza);
		}

		return null;
	}

	/**
	 * Parte reutilizável da FUNCTION MBCC_UTILIZACAO_SALA
	 * 
	 * @param dataCirurgia
	 * @param sala
	 * @param unidadeSala
	 * @param matriculaResp
	 * @param vinCodigoResp
	 * @param natureza
	 * @return
	 */
	private DominioUtilizacaoSala verificarUtilizacaoSalaDominioUtilizacaoSala(Date dataCirurgia, Short sala, Short unidadeSala, Integer matriculaResp, Short vinCodigoResp,
			DominioNaturezaFichaAnestesia natureza) {

		MbcCaracteristicaSalaCirg caracteristicaSalaCirgDia = this.getMbcCaracteristicaSalaCirgDAO().buscarCaracteristicaPorTurnoDiaSemanSalaCirurgica(dataCirurgia, sala,
				unidadeSala);
		if (caracteristicaSalaCirgDia == null) {
			// NÃO Encontrou turno
			return DominioUtilizacaoSala.NPR;
		}

		List<MbcCaractSalaEsp> listaCaractSalaEspProf = this.getMbcCaractSalaEspDAO().pesquisarCaractSalaEspAtivasProfissionalCirurgicoUnidade(caracteristicaSalaCirgDia.getSeq(),
				matriculaResp, vinCodigoResp, unidadeSala);
		if (!listaCaractSalaEspProf.isEmpty()) {
			// ENCONTROU profissional
			return DominioUtilizacaoSala.PRE;
		}

		List<MbcSubstEscalaSala> listaSubstEscalaSala = this.getMbcSubstEscalaSalaDAO().pesquisarSubstitutoPorCaracProfAtuaUniCirurData(caracteristicaSalaCirgDia.getSeq(),
				matriculaResp, vinCodigoResp, unidadeSala, dataCirurgia);
		if (!listaSubstEscalaSala.isEmpty()) {
			// ENCONTROU substituto
			return DominioUtilizacaoSala.PRE;
		} else {
			// NÃO encontrou substituto
			return DominioUtilizacaoSala.NPR;
		}

	}

	/**
	 * PROCEDURE MBCK_PCG_RN.RN_PCGP_VER_RESPONS
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void verificarResponsavel(Integer crgSeq) throws ApplicationBusinessException {

		List<MbcProfCirurgias> listProfCirurgias = this.getMbcProfCirurgiasDAO().obterProfCirurgiasPorCrgSeq(crgSeq, null);

		if (listProfCirurgias == null || listProfCirurgias.isEmpty()) {
			/* Deve haver um profissional com indicador de responsável, obrigatoriamente. */
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00332);
		} else if (listProfCirurgias.size() > 1) {
			/* Deve haver somente um profissional com indicador de responsável. */
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00332);
		}

		DominioFuncaoProfissional[] funcoes = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF };
		List<MbcProfCirurgias> listProfCirurgiasFuncoes = this.getMbcProfCirurgiasDAO().obterProfCirurgiasPorCrgSeq(crgSeq, funcoes);
		if (listProfCirurgiasFuncoes == null || listProfCirurgiasFuncoes.isEmpty()) {
			/* O profissional com indicador de responsável deve ser médico contratado ou professor */
			throw new ApplicationBusinessException(MbcProfCirurgiasRNExceptionCode.MBC_00334);
		}
	}
	
	//Verifica se o Profissional indicado CirurgiaTelaProfissionalVOcomo responsável está é um médico professor ou contratado (RN09)
	public boolean validaProfissionalResp(List<CirurgiaTelaProfissionalVO> listaProfissionaisVO){
		CirurgiaTelaProfissionalVO responsavel = getProfissionalVoResponsavel(listaProfissionaisVO);
		if(responsavel.getFuncaoProfissional().getCodigo().equals(DominioFuncaoProfissional.MCO.getCodigo()) ||
		   responsavel.getFuncaoProfissional().getCodigo().equals(DominioFuncaoProfissional.MPF.getCodigo())){
		   return true;
		}
		return false;
	}
	
	private CirurgiaTelaProfissionalVO getProfissionalVoResponsavel(List<CirurgiaTelaProfissionalVO> listaProfissionaisVO){
		for(CirurgiaTelaProfissionalVO profissionalVO : listaProfissionaisVO){
			if(profissionalVO.getIndResponsavel()){
				return profissionalVO; }
		}
		return null;
	}
	

	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionEquipe(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		List<SuggestionListaCirurgiaVO> equipes = getMbcProfCirurgiasDAO().pesquisarSuggestionEquipe(unidade, dtProcedimento, filtro, indResponsavel);
		Long vTemEquipe = this.getPrescricaoMedicaFacade().pesquisarAssociacoesPorServidorCount(servidorLogado);
		if(vTemEquipe > Long.valueOf("0")){
			equipes.add(0, new SuggestionListaCirurgiaVO("EQUIPES DO USUÁRIO", true,  servidorLogado.getId().getMatricula(),  servidorLogado.getId().getVinCodigo()));
		}
		return equipes;
	}
	
	public Long pesquisarSuggestionEquipeCount(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Long count = getMbcProfCirurgiasDAO().pesquisarSuggestionEquipeCount(unidade, dtProcedimento, filtro, indResponsavel);
		Long vTemEquipe = this.getPrescricaoMedicaFacade().pesquisarAssociacoesPorServidorCount(servidorLogado);
		if(vTemEquipe > Long.valueOf("0")){
			count++;
		}
		return count;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return iSolicitacaoExameFacade;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}

	protected ICascaFacade getICascaFacade() {
		return this.iCascaFacade;
	}

	protected MbcProfCirurgiaJnDAO getMbcProfCirurgiaJnDAO() {
		return mbcProfCirurgiaJnDAO;
	}

	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}

	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}

	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO() {
		return mbcSubstEscalaSalaDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}	

}
