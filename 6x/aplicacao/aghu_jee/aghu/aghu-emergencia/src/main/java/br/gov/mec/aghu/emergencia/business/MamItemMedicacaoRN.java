package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamItemMedicacaoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio relacionadas à entidade MamItemMedicacao
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamItemMedicacaoRN extends BaseBusiness {
	private static final long serialVersionUID = -2628105757014622786L;

	public enum MamItemMedicacaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL, //
		MENSAGEM_SUCESSO_ADICAO_MEDICACAO, //
		SUCESSO_ALTERACAO_SITUACAO_MEDICACAO, //
		MENSAGEM_MEDICACAO_JA_EXISTENTE, //
		MENSAGEM_SUCESSO_EXCLUSAO_MEDICACAO, //
		;
	}

	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamDescritorDAO mamDescritorDAO;

	@Inject
	private MamItemMedicacaoDAO mamItemMedicacaoDAO;

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
	public List<ItemObrigatorioVO> pesquisarItensMedicacoes(List<MamObrigatoriedade> mamObrigatoriedades) throws ApplicationBusinessException {

		List<ItemObrigatorioVO> result = new ArrayList<ItemObrigatorioVO>();

		for (MamObrigatoriedade mamObrigatoriedade : mamObrigatoriedades) {
			if (mamObrigatoriedade.getMamItemMedicacao() != null) {
				// Popula a lista de medicacoes
				result.add(this.buscarItemMedicacao(mamObrigatoriedade));
			}
		}

		return result;
	}

	/**
	 * Busca o item de medicação obrigatório à partir de uma obrigatoriedade
	 * 
	 * @param mamObrigatoriedade
	 * @return
	 */
	private ItemObrigatorioVO buscarItemMedicacao(MamObrigatoriedade mamObrigatoriedade) throws ApplicationBusinessException {
		if (mamObrigatoriedade.getMamItemMedicacao() != null) {
			ItemObrigatorioVO itemObrigatorioVO = new ItemObrigatorioVO(mamObrigatoriedade.getSeq(), mamObrigatoriedade.getIndSituacao().isAtivo());
			itemObrigatorioVO.setSeqItem(mamObrigatoriedade.getMamItemMedicacao().getSeq());
			itemObrigatorioVO.setDescricao(mamObrigatoriedade.getMamItemMedicacao().getDescricao());
			itemObrigatorioVO.setSigla(null);
			return itemObrigatorioVO;
		}
		return null;
	}

	/**
	 * Monta um registro de MamObrigatoriedade, executando trigger de pos-update
	 * 
	 * RN11 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public MamObrigatoriedade montarRegistroMedicacao(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = new MamObrigatoriedade();

		// 1. Setar o SEQ do registro selecionado no item 16 do quadro descritivo no campo MAM_OBRIGATORIEDADES.MDM_SEQ
		MamItemMedicacao mamItemMedicacao = mamItemMedicacaoDAO.obterPorChavePrimaria(itemObrigatorioVO.getSeqItem());
		mamObrigatoriedade.setMamItemMedicacao(mamItemMedicacao);
		mamObrigatoriedade.setIndSituacao(DominioSituacao.getInstance(itemObrigatorioVO.getIndSituacaoObr()));

		// 2. Setar o SEQ de MAM_DESCRITORES selecionado no fieldset “Descritores” no campo MAM_OBRIGATORIEDADES.DCT_SEQ
		MamDescritor descritor = mamDescritorDAO.obterPorChavePrimaria(seqDesc);
		mamObrigatoriedade.setMamDescritor(descritor);

		// 3. Se o registro já tiver sido cadastrado apresentar mensagem “MENSAGEM_MEDICACAO_JA_EXISTENTE” e cancelar o processamento
		// Verificar se existe algum MAM_OBRIGATORIEDADES da mesma MAM_DESCRITORES que já tenha o mesmo MDM_SEQ
		boolean existsItem = mamObrigatoriedadeDAO.existsItemMedicacaoPorDescritor(itemObrigatorioVO.getSeqItem(), descritor);
		if (existsItem) {
			throw new ApplicationBusinessException(MamItemMedicacaoRNExceptionCode.MENSAGEM_MEDICACAO_JA_EXISTENTE);
		}

		return mamObrigatoriedade;
	}
}