package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamItemGeralDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio relacionadas à entidade MamItemGeral
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamItemGeralRN extends BaseBusiness {
	private static final long serialVersionUID = 5668360712677386125L;

	public enum MamItemGeralRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL, //
		MENSAGEM_SUCESSO_ADICAO_DADO_GERAL, //
		SUCESSO_ALTERACAO_SITUACAO_DADO_GERAL, //
		MENSAGEM_DADO_GERAL_JA_EXISTENTE, //
		MENSAGEM_SUCESSO_EXCLUSAO_DADO_GERAL, //
		;
	}

	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamDescritorDAO mamDescritorDAO;

	@Inject
	private MamItemGeralDAO mamItemGeralDAO;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Busca a lista de itens obrigatórios gerais à partir das obrigatoriedades
	 * 
	 * @param mamObrigatoriedades
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemObrigatorioVO> pesquisarItensGerais(List<MamObrigatoriedade> mamObrigatoriedades) throws ApplicationBusinessException {
		List<ItemObrigatorioVO> result = new ArrayList<ItemObrigatorioVO>();
		for (MamObrigatoriedade mamObrigatoriedade : mamObrigatoriedades) {
			if (mamObrigatoriedade.getMamItemGeral() != null) {
				// Popula a lista de itens gerais
				result.add(this.buscarItemGeral(mamObrigatoriedade));
			}
		}
		return result;
	}

	/**
	 * Busca o item geral obrigatório à partir de uma obrigatoriedade
	 * 
	 * @param mamObrigatoriedade
	 * @return
	 */
	private ItemObrigatorioVO buscarItemGeral(MamObrigatoriedade mamObrigatoriedade) throws ApplicationBusinessException {
		if (mamObrigatoriedade.getMamItemGeral() != null) {
			ItemObrigatorioVO itemObrigatorioVO = new ItemObrigatorioVO(mamObrigatoriedade.getSeq(), mamObrigatoriedade.getIndSituacao().isAtivo());
			itemObrigatorioVO.setSeqItem(mamObrigatoriedade.getMamItemGeral().getSeq());
			itemObrigatorioVO.setDescricao(mamObrigatoriedade.getMamItemGeral().getDescricao());
			itemObrigatorioVO.setSigla(null);
			return itemObrigatorioVO;
		}
		return null;
	}

	/**
	 * Monta um registro de MamObrigatoriedade, executando trigger de pos-update
	 * 
	 * RN14 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public MamObrigatoriedade montarRegistroGeral(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = new MamObrigatoriedade();

		// 1. Setar o SEQ do registro selecionado no item 23 do quadro descritivo no campo MAM_OBRIGATORIEDADES.ITG_SEQ
		MamItemGeral mamItemGeral = mamItemGeralDAO.obterPorChavePrimaria(itemObrigatorioVO.getSeqItem());
		mamObrigatoriedade.setMamItemGeral(mamItemGeral);
		mamObrigatoriedade.setIndSituacao(DominioSituacao.getInstance(itemObrigatorioVO.getIndSituacaoObr()));

		// 2. Setar o SEQ de MAM_DESCRITORES selecionado no fieldset “Descritores” no campo MAM_OBRIGATORIEDADES.DCT_SEQ
		MamDescritor descritor = mamDescritorDAO.obterPorChavePrimaria(seqDesc);
		mamObrigatoriedade.setMamDescritor(descritor);

		// 3. Se o registro já tiver sido cadastrado apresentar mensagem “MENSAGEM_DADO_GERAL_JA_EXISTENTE” e cancelar o processamento
		// Verificar se existe algum MAM_OBRIGATORIEDADES da mesma MAM_DESCRITORES que já tenha o mesmo ITG_SEQ
		boolean existsItem = mamObrigatoriedadeDAO.existsItemGeralPorDescritor(itemObrigatorioVO.getSeqItem(), descritor);
		if (existsItem) {
			throw new ApplicationBusinessException(MamItemGeralRNExceptionCode.MENSAGEM_DADO_GERAL_JA_EXISTENTE);
		}

		return mamObrigatoriedade;
	}
}
