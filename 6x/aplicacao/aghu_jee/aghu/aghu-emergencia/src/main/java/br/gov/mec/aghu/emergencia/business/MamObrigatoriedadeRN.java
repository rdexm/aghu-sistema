package br.gov.mec.aghu.emergencia.business;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamObrigatoriedadeJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamObrigatoriedade
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamObrigatoriedadeRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamObrigatoriedadeRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL, //
		;
	}

	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamObrigatoriedadeJnDAO mamObrigatoriedadeJnDAO;

	@EJB
	private MamItemGeralRN mamItemGeralRN;

	@EJB
	private MamItemExameRN mamItemExameRN;

	@EJB
	private MamItemMedicacaoRN mamItemMedicacaoRN;

	@EJB
	private MamItemSinalVitalRN mamItemSinalVitalRN;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Popula as listas de itens obrigatórios à partir de um descritor
	 * 
	 * RN01 de #32658
	 * 
	 * @param mamDescritor
	 *            Descritor utilizado como filtro
	 * @param itensSinaisVitais
	 *            Lista de itens de sinais vitais a ser populada
	 * @param itensExames
	 *            Lista de itens de exames a ser populada
	 * @param itensMedicamecoes
	 *            Lista de itens de medicacoes vitais a ser populada
	 * @param itensGerais
	 *            Lista de itens gerais a ser populada
	 * @throws ApplicationBusinessException
	 */
	public void popularItensObrigatorios(MamDescritor mamDescritor, List<ItemObrigatorioVO> itensSinaisVitais, List<ItemObrigatorioVO> itensExames,
			List<ItemObrigatorioVO> itensMedicamecoes, List<ItemObrigatorioVO> itensGerais) throws ApplicationBusinessException {

		// Limpa as listas
		itensSinaisVitais.clear();
		itensExames.clear();
		itensMedicamecoes.clear();
		itensGerais.clear();

		// Busca as obrigatoriedades
		List<MamObrigatoriedade> mamObrigatoriedades = mamObrigatoriedadeDAO.pesquisarPorMamDescritor(mamDescritor);

		// Popula a lista de sinais vitais
		itensSinaisVitais.addAll(mamItemSinalVitalRN.pesquisarItensSinaisVitais(mamObrigatoriedades));
		Collections.sort(itensSinaisVitais);

		// Popula a lista de exames
		itensExames.addAll(mamItemExameRN.pesquisarItensExames(mamObrigatoriedades));
		Collections.sort(itensExames);

		// Popula a lista de medicacoes
		itensMedicamecoes.addAll(mamItemMedicacaoRN.pesquisarItensMedicacoes(mamObrigatoriedades));
		Collections.sort(itensMedicamecoes);

		// Popula a lista de itens gerais
		itensGerais.addAll(mamItemGeralRN.pesquisarItensGerais(mamObrigatoriedades));
		Collections.sort(itensGerais);
	}

	/**
	 * Insere um registro de MamObrigatoriedade, executando trigger de pré-insert
	 * 
	 * RN02 de #32658
	 * 
	 * @param mamObrigatoriedade
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MamObrigatoriedade mamObrigatoriedade) throws ApplicationBusinessException {
		// 4. Executar RN03
		this.preInsert(mamObrigatoriedade);
		// 5. Inserir registro na tabela MAM_OBRIGATORIEDADES
		mamObrigatoriedadeDAO.persistir(mamObrigatoriedade);
	}

	/**
	 * Pré-insert de MamObrigatoriedade
	 * 
	 * RN03 de #32658
	 * 
	 * @ORADB MAM_OBRIGATORIEDADES.MAMT_OBR_BRI
	 * 
	 * @param mamObrigatoriedade
	 */
	private void preInsert(MamObrigatoriedade mamObrigatoriedade) {
		// 1. Seta no campo CRIADO_EM a data atual
		mamObrigatoriedade.setCriadoEm(new Date());
		// 2. Seta no campo SER_VIN_CODIGO o código do vínculo do usuário logado no sistema
		mamObrigatoriedade.setSerVinCodigo(usuario.getVinculo());
		// 3. Seta no campo SER_MATRICULA a matrícula do usuário logado no sistema
		mamObrigatoriedade.setSerMatricula(usuario.getMatricula());
	}

	/**
	 * Pos-Update de MamObrigatoriedade
	 * 
	 * RN05 de #32285
	 * 
	 * @ORADB MAM_OBRIGATORIEDADES.MAMT_OBR_ARU
	 * 
	 * @param mamObrigatoriedade
	 */
	private void posUpdate(MamObrigatoriedade mamObrigatoriedadeOriginal, MamObrigatoriedade mamObrigatoriedade) {
		// 1. Se algum campo tiver sido modificado para o registro corrente, inserir o objeto original na tabela MAM_OBRIGATORIEDADES_JN setando ‘UPD’ na coluna JN_OPERATION
		if (CoreUtil.modificados(mamObrigatoriedade.getMamDescritor(), mamObrigatoriedadeOriginal.getMamDescritor())
				|| CoreUtil.modificados(mamObrigatoriedade.getIndSituacao(), mamObrigatoriedadeOriginal.getIndSituacao())
				|| CoreUtil.modificados(mamObrigatoriedade.getIceSeq(), mamObrigatoriedadeOriginal.getIceSeq())
				|| CoreUtil.modificados(mamObrigatoriedade.getMamItemSinalVital(), mamObrigatoriedadeOriginal.getMamItemSinalVital())
				|| CoreUtil.modificados(mamObrigatoriedade.getMamItemMedicacao(), mamObrigatoriedadeOriginal.getMamItemMedicacao())
				|| CoreUtil.modificados(mamObrigatoriedade.getMamItemExame(), mamObrigatoriedadeOriginal.getMamItemExame())
				|| CoreUtil.modificados(mamObrigatoriedade.getMamItemGeral(), mamObrigatoriedadeOriginal.getMamItemGeral())) {
			this.inserirJournalMamObrigatoriedade(mamObrigatoriedadeOriginal, DominioOperacoesJournal.UPD);
		}
	}

	/**
	 * Insere um registro na journal de MamObrigatoriedade
	 * 
	 * @param obrOriginal
	 * @param operacao
	 */
	private void inserirJournalMamObrigatoriedade(MamObrigatoriedade obrOriginal, DominioOperacoesJournal operacao) {
		MamObrigatoriedadeJn mamObrigatoriedadeJn = BaseJournalFactory.getBaseJournal(operacao, MamObrigatoriedadeJn.class, usuario.getLogin());
		mamObrigatoriedadeJn.setSeq(obrOriginal.getSeq());
		mamObrigatoriedadeJn.setIndSituacao(obrOriginal.getIndSituacao());
		mamObrigatoriedadeJn.setCriadoEm(obrOriginal.getCriadoEm());
		mamObrigatoriedadeJn.setSerMatricula(obrOriginal.getSerMatricula());
		mamObrigatoriedadeJn.setSerVinCodigo(obrOriginal.getSerVinCodigo());
		mamObrigatoriedadeJn.setDctSeq(obrOriginal.getMamDescritor().getSeq());
		mamObrigatoriedadeJn.setIceSeq(obrOriginal.getIceSeq());
		mamObrigatoriedadeJn.setSviSeq(obrOriginal.getMamItemSinalVital() != null ? obrOriginal.getMamItemSinalVital().getSeq() : null);
		mamObrigatoriedadeJn.setMdmSeq(obrOriginal.getMamItemMedicacao() != null ? obrOriginal.getMamItemMedicacao().getSeq() : null);
		mamObrigatoriedadeJn.setEmsSeq(obrOriginal.getMamItemExame() != null ? obrOriginal.getMamItemExame().getSeq() : null);
		mamObrigatoriedadeJn.setItgSeq(obrOriginal.getMamItemGeral() != null ? obrOriginal.getMamItemGeral().getSeq() : null);
		mamObrigatoriedadeJnDAO.persistir(mamObrigatoriedadeJn);
	}

	/**
	 * Ativa/Desativa um registro de MamObrigatoriedade
	 * 
	 * RN04 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void ativarDesativar(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamObrigatoriedadeDAO.obterPorChavePrimaria(itemObrigatorioVO.getSeqObr());

		MamObrigatoriedade mamObrigatoriedadeOriginal = this.mamObrigatoriedadeDAO.obterOriginal(mamObrigatoriedade);

		// 1. Se o valor do campo IND_SITUACAO for ‘A’, setar ‘I’ e vice-versa
		mamObrigatoriedade.setIndSituacao(DominioSituacao.getInstance(!mamObrigatoriedade.getIndSituacao().isAtivo()));

		// 2. Executar atualização de registro em MAM_OBRIGATORIEDADES
		mamObrigatoriedadeDAO.persistir(mamObrigatoriedade);

		// 3. Executar RN05
		this.posUpdate(mamObrigatoriedadeOriginal, mamObrigatoriedade);
	}

	/**
	 * Exclui um registro de MamObrigatoriedade
	 * 
	 * RN06 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void remover(ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamObrigatoriedadeDAO.obterPorChavePrimaria(itemObrigatorioVO.getSeqObr());

		// 1. Exclui o registro em MAM_OBRIGATORIEDADES. (Usar método remove da DAO correspondente)
		mamObrigatoriedadeDAO.remover(mamObrigatoriedade);

		// 2. Executa RN07
		this.posDelete(mamObrigatoriedade);
	}

	/**
	 * Pos-Delete de MamObrigatoriedade
	 * 
	 * RN07 de #32285
	 * 
	 * @ORADB MAM_OBRIGATORIEDADES.MAMT_OBR_ARD
	 * 
	 * @param mamObrigatoriedade
	 */
	private void posDelete(MamObrigatoriedade mamObrigatoriedade) {
		// 1. Inserir o objeto original na tabela MAM_OBRIGATORIEDADES_JN setando ‘DEL’ na coluna JN_OPERATION
		this.inserirJournalMamObrigatoriedade(mamObrigatoriedade, DominioOperacoesJournal.DEL);
	}

	/**
	 * Insere um registro de MamObrigatoriedade para Sinal Vital, executando trigger de pré-insert
	 * 
	 * RN02 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void inserirObrigatoriedadeSinalVital(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamItemSinalVitalRN.montarRegistroSinalVital(seqDesc, itemObrigatorioVO);
		this.inserir(mamObrigatoriedade);
	}

	/**
	 * Insere um registro de MamObrigatoriedade para Exame, executando trigger de pré-insert
	 * 
	 * RN08 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void inserirObrigatoriedadeExame(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamItemExameRN.montarRegistroExame(seqDesc, itemObrigatorioVO);
		this.inserir(mamObrigatoriedade);
	}

	/**
	 * Insere um registro de MamObrigatoriedade para Medicacao, executando trigger de pré-insert
	 * 
	 * RN11 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void inserirObrigatoriedadeMedicacao(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamItemMedicacaoRN.montarRegistroMedicacao(seqDesc, itemObrigatorioVO);
		this.inserir(mamObrigatoriedade);
	}

	/**
	 * Insere um registro de MamObrigatoriedade para Geral, executando trigger de pré-insert
	 * 
	 * RN14 de #32658
	 * 
	 * @param seqDesc
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public void inserirObrigatoriedadeGeral(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = mamItemGeralRN.montarRegistroGeral(seqDesc, itemObrigatorioVO);
		this.inserir(mamObrigatoriedade);
	}
}
