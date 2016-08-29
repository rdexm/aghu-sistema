package br.gov.mec.aghu.emergencia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamGravidadeJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamGravidade
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamGravidadeRN extends BaseBusiness {
	
	private static final long serialVersionUID = -6284647322559671734L;

	private static final Log LOG = LogFactory.getLog(MamGravidadeRN.class);
	

	public enum MamGravidadeRNExceptionCode implements BusinessExceptionCode {
		MAM_ERRO_GRAU_GRAVIDADE_PROTOCOLO_BLOQUEADO, //
		MAM_SUCESSO_CAD_GRAU_GRAVIDADE, //
		MAM_SUCESSO_ALTERACAO_GRAU_GRAVIDADE, //
		MENSAGEM_GRAVIDADE_JA_CADASTRADA, //
		MENSAGEM_SUCESSO_ALTERACAO_ORDEM_GRAVIDADE, //
		MAM_ERRO_GRAU_GRAVIDADE_ORDEM_EXISTENTE,
		OPTIMISTIC_LOCK,
		;
	}

	@Inject
	private MamGravidadeDAO mamGravidadeDAO;

	@Inject
	private MamGravidadeJnDAO mamGravidadeJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Insere/Altera um registro de MamGravidade
	 * 
	 * RN01 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamGravidade mamGravidade) throws ApplicationBusinessException {
		LOG.info("MamGravidadeRN.persistir(MamGravidade)");
		
		// 1. Se o valor do campo IND_BLOQUEADO do protocolo selecionado no item 1 do quadro descritivo for ‘A’ apresentar mensagem “MAM_ERRO_PROTOCOLO_BLOQUEADO” e cancela o
		// processamento.
		this.validarBloqueio(mamGravidade);
		
		//incluida regra pra validar existência de ordem já cadastrada
		this.validarOrdem(mamGravidade);
		
		if (mamGravidade.getSeq() == null) {
			// Se estiver realizando inserção:

			// 2. Executa consulta C3. Se retornar resultados apresenta mensagem “MENSAGEM_GRAVIDADE_JA_CADASTRADA” e cancela o processamento.
			Boolean existsGravidade = mamGravidadeDAO.existsGravidadePorProtocoloDescricao(mamGravidade.getProtClassifRisco(), mamGravidade.getDescricao());
			if (existsGravidade) {
				throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.MENSAGEM_GRAVIDADE_JA_CADASTRADA);
			}

			// 3. Executa RN02
			this.mamtGrvBri(mamGravidade);

			// 4. Insere registro em MAM_GRAVIDADES
			mamGravidadeDAO.persistir(mamGravidade);
		} else {
			// Se estiver realizando atualização:

			// 2. Executa consulta C3. Se retornar resultados apresenta mensagem “MENSAGEM_GRAVIDADE_JA_CADASTRADA” e cancela o processamento.
			Boolean existsGravidade = mamGravidadeDAO.existsGravidadePorSeqProtocoloDescricao(mamGravidade.getSeq(), mamGravidade.getProtClassifRisco(),
					mamGravidade.getDescricao());
			if (existsGravidade) {
				throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.MENSAGEM_GRAVIDADE_JA_CADASTRADA);
			}

			// 4. Executa RN03
			MamGravidade mamGravidadeOriginal = this.mamGravidadeDAO.obterOriginal(mamGravidade);
			this.mamtGrvAru(mamGravidade, mamGravidadeOriginal);

			// 3. Executa update na tabela MAM_GRAVIDADES
			mamGravidadeDAO.atualizar(mamGravidade);
		}
	}

	/**
	 * Pre-Insert de MamGravidade
	 * 
	 * RN02 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @ORABD MAM_GRAVIDADES.MAMT_GRV_BRI
	 * 
	 * @param mamGravidade
	 */
	private void mamtGrvBri(MamGravidade mamGravidade) {
		mamGravidade.setCriadoEm(new Date());		
		mamGravidade.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
	}

	/**
	 * Pos-Update de MamGravidade
	 * 
	 * RN03 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @ORADB MAM_GRAVIDADES.MAMT_GRV_ARU
	 * 
	 * @param mamGravidade
	 */
	private void mamtGrvAru(MamGravidade mamGravidade, MamGravidade mamGravidadeOriginal) {
		if (CoreUtil.modificados(mamGravidade.getDescricao(), mamGravidadeOriginal.getDescricao())
				|| CoreUtil.modificados(mamGravidade.getOrdem(), mamGravidadeOriginal.getOrdem())
				|| CoreUtil.modificados(mamGravidade.getTempoEspera(), mamGravidadeOriginal.getTempoEspera())
				|| CoreUtil.modificados(mamGravidade.getCodCor(), mamGravidadeOriginal.getCodCor())
				|| CoreUtil.modificados(mamGravidade.getIndPermiteSaida(), mamGravidadeOriginal.getIndPermiteSaida())
				|| CoreUtil.modificados(mamGravidade.getIndUsoTriagem(), mamGravidadeOriginal.getIndUsoTriagem())
				|| CoreUtil.modificados(mamGravidade.getIndSituacao(), mamGravidadeOriginal.getIndSituacao())) {
			this.inserirJournalMamGravidade(mamGravidadeOriginal, DominioOperacoesJournal.UPD);
		}

	}

	/**
	 * Insere um registro na journal de MamGravidade
	 * 
	 * @param mamGravidadeOriginal
	 * @param operacao
	 */
	private void inserirJournalMamGravidade(MamGravidade mamGravidadeOriginal, DominioOperacoesJournal operacao) {
		MamGravidadeJn mamGravidadeJn = BaseJournalFactory.getBaseJournal(operacao, MamGravidadeJn.class, usuario.getLogin());
		mamGravidadeJn.setAtributoVisual(mamGravidadeOriginal.getAtributoVisual());
		mamGravidadeJn.setCampoTela(mamGravidadeOriginal.getCampoTela());
		mamGravidadeJn.setCodCor(mamGravidadeOriginal.getCodCor());
		mamGravidadeJn.setCor(mamGravidadeOriginal.getCor());
		mamGravidadeJn.setCriadoEm(mamGravidadeOriginal.getCriadoEm());
		mamGravidadeJn.setDescricao(mamGravidadeOriginal.getDescricao());
		mamGravidadeJn.setIcone(mamGravidadeOriginal.getIcone());
		mamGravidadeJn.setIndPermiteSaida(mamGravidadeOriginal.getIndPermiteSaida());
		mamGravidadeJn.setIndSituacao(mamGravidadeOriginal.getIndSituacao());
		mamGravidadeJn.setIndUsoTriagem(mamGravidadeOriginal.getIndUsoTriagem());
		mamGravidadeJn.setOrdem(mamGravidadeOriginal.getOrdem());
		mamGravidadeJn.setPcrSeq(mamGravidadeOriginal.getProtClassifRisco().getSeq());
		mamGravidadeJn.setSeq(mamGravidadeOriginal.getSeq());
		mamGravidadeJn.setSerMatricula(mamGravidadeOriginal.getRapServidores().getId().getMatricula());
		mamGravidadeJn.setSerVinCodigo(mamGravidadeOriginal.getRapServidores().getId().getVinCodigo());
		mamGravidadeJn.setTempoEspera(mamGravidadeOriginal.getTempoEspera());
		mamGravidadeJnDAO.persistir(mamGravidadeJn);
	}

	/**
	 * Sobe a ordem de um registro de MamGravidade
	 * 
	 * RN04 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @param gravidades
	 * @throws ApplicationBusinessException
	 */
	public boolean subirOrdem(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException {
		try {
			// 1. Se o valor do campo IND_BLOQUEADO do protocolo selecionado no item 1 do quadro descritivo for ‘A’ apresentar mensagem
			// “MAM_ERRO_PROTOCOLO_BLOQUEADO” e cancela o
			// processamento
			this.validarBloqueio(mamGravidade);

			// 2. Se não for o primeiro registro do grid, trocar o valor do campo MAM_GRAVIDADES.ORDEM com o valor do mesmo campo do
			// registro acima. Caso seja o primeiro registro não
			// executar ação alguma.
			int indice = gravidades.indexOf(mamGravidade);
			if (indice > 0) {
				indice--;
				MamGravidade mamGravidadeAnterior = gravidades.get(indice);
				this.trocarOrdemGravidade(mamGravidade, mamGravidadeAnterior);
				this.flush();
				return true;
			}
			return false;
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.OPTIMISTIC_LOCK);
		}
	}

	/**
	 * Descer a ordem de um registro de MamGravidade
	 * 
	 * RN05 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamGravidade
	 * @param gravidades
	 * @throws ApplicationBusinessException
	 */
	public boolean descerOrdem(MamGravidade mamGravidade, List<MamGravidade> gravidades) throws ApplicationBusinessException {
		try {
			// 1. Se o valor do campo IND_BLOQUEADO do protocolo selecionado no item 1 do quadro descritivo for ‘A’ apresentar mensagem
			// “MAM_ERRO_PROTOCOLO_BLOQUEADO” e cancela o
			// processamento
			this.validarBloqueio(mamGravidade);

			// 2. Se não for o primeiro registro do grid, trocar o valor do campo MAM_GRAVIDADES.ORDEM com o valor do mesmo campo do
			// registro acima. Caso seja o primeiro registro não
			// executar ação alguma.
			int indice = gravidades.indexOf(mamGravidade);
			int ultimo = gravidades.size() - 1;
			if (indice > -1 && indice < ultimo) {
				indice++;
				MamGravidade mamGravidadePosterior = gravidades.get(indice);
				this.trocarOrdemGravidade(mamGravidade, mamGravidadePosterior);
				this.flush();
				return true;
			}
			return false;
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.OPTIMISTIC_LOCK);
		}
	}

	/**
	 * Troca a ordem de dois registros
	 * 
	 * RN04 e RN05
	 * 
	 * @param mamGravidade
	 * @param mamGravidadeAnterior
	 */
	private void trocarOrdemGravidade(MamGravidade mamGravidade, MamGravidade mamGravidadeAnterior) {
		MamGravidade mamGravidadeOriginal = this.mamGravidadeDAO.obterOriginal(mamGravidade);
		MamGravidade mamGravidadeAnteriorOriginal = this.mamGravidadeDAO.obterOriginal(mamGravidadeAnterior);

		Short ordemAnterior = mamGravidadeAnterior.getOrdem();
		mamGravidadeAnterior.setOrdem(mamGravidade.getOrdem());
		mamGravidade.setOrdem(ordemAnterior);

		// 4. Executa RN03
		this.mamtGrvAru(mamGravidade, mamGravidadeOriginal);

		// 3. Executa update na tabela MAM_GRAVIDADES para o registro em que foi clicado no item 12 do quadro descritivo
		mamGravidadeDAO.atualizar(mamGravidade);

		// 6. Executa RN03
		this.mamtGrvAru(mamGravidadeAnterior, mamGravidadeAnteriorOriginal);

		// 5. Executa update na tabela MAM_GRAVIDADES para o registro acima daquele em que foi clicado no item 12 do quadro descritivo
		mamGravidadeDAO.atualizar(mamGravidadeAnterior);
	}

	private void validarBloqueio(MamGravidade mamGravidade) throws ApplicationBusinessException {
		if (mamGravidade.getProtClassifRisco().getIndBloqueado().isAtivo()) {
			throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.MAM_ERRO_GRAU_GRAVIDADE_PROTOCOLO_BLOQUEADO, mamGravidade.getProtClassifRisco()
					.getDescricao());
		}
	}
	
	private void validarOrdem(MamGravidade mamGravidade) throws ApplicationBusinessException {
		boolean ordem = false;
		MamGravidade mamGravidadeOriginal = this.mamGravidadeDAO.obterOriginal(mamGravidade);
		
		if (mamGravidade.getSeq() == null || (mamGravidade.getSeq() != null && mamGravidadeOriginal != null && CoreUtil.modificados(mamGravidade.getOrdem(), mamGravidadeOriginal.getOrdem()))){
			ordem = this.mamGravidadeDAO.verificarGravidadeComMesmaOrdem(mamGravidade.getProtClassifRisco(), mamGravidade.getOrdem());
			if (ordem) {
				throw new ApplicationBusinessException(MamGravidadeRNExceptionCode.MAM_ERRO_GRAU_GRAVIDADE_ORDEM_EXISTENTE, mamGravidade.getOrdem());
			}
		}
	}
}
