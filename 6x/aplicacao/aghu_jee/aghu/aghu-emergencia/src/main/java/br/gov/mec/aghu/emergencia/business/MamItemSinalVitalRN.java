package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.controlepaciente.vo.DadosSinaisVitaisVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Regras de negócio relacionadas à entidade MamItemSinalVital
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamItemSinalVitalRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamItemSinalVitalRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL, //
		MENSAGEM_SUCESSO_ADICAO_SINAL_VITAL, //
		SUCESSO_ALTERACAO_SITUACAO_SINAL_VITAL, //
		MENSAGEM_SINAL_VITAL_JA_EXISTENTE, //
		MENSAGEM_SUCESSO_EXCLUSAO_SINAL_VITAL, //
		;
	}

	@EJB
	private IControlePacienteService controlePacienteService;

	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamDescritorDAO mamDescritorDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Busca a lista de itens obrigatórios de sinais vitais à partir das obrigatoriedades
	 * 
	 * @param mamObrigatoriedades
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemObrigatorioVO> pesquisarItensSinaisVitais(List<MamObrigatoriedade> mamObrigatoriedades) throws ApplicationBusinessException {

		List<ItemObrigatorioVO> result = new ArrayList<ItemObrigatorioVO>();

		// Busca os itens de sinais vitais do serviço #35290
		List<DadosSinaisVitaisVO> dadosSinaisVitais = this.buscarListaSinaisVitais(mamObrigatoriedades);

		for (MamObrigatoriedade mamObrigatoriedade : mamObrigatoriedades) {
			if (mamObrigatoriedade.getIceSeq() != null) {
				// Busca na lista o elemento correspondente
				DadosSinaisVitaisVO dadosSinaisVitaisVO = this.obterElementoDaLista(mamObrigatoriedade, dadosSinaisVitais);
				// Popula a lista de sinais vitais
				ItemObrigatorioVO sinalVital = this.buscarItemSinaisVitais(mamObrigatoriedade, dadosSinaisVitaisVO);
				if (sinalVital != null) {
					result.add(sinalVital);
				}
			}
		}

		return result;
	}

	/**
	 * Busca um elemento na lista pelo id
	 * 
	 * @param mamObrigatoriedade
	 * @param dadosSinaisVitais
	 * @return
	 */
	private DadosSinaisVitaisVO obterElementoDaLista(MamObrigatoriedade mamObrigatoriedade, List<DadosSinaisVitaisVO> dadosSinaisVitais) {

		DadosSinaisVitaisVO dadosSinaisVitaisVO = new DadosSinaisVitaisVO();
		dadosSinaisVitaisVO.setSeq(mamObrigatoriedade.getIceSeq());
		int posicao = dadosSinaisVitais.indexOf(dadosSinaisVitaisVO);

		dadosSinaisVitaisVO = (posicao > -1 ? dadosSinaisVitais.get(posicao) : null);

		return dadosSinaisVitaisVO;
	}

	/**
	 * Monta a lista de itens obrigatorios de sinais vitais à partir da lista de obrigatoriedades
	 * 
	 * @param mamObrigatoriedades
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<DadosSinaisVitaisVO> buscarListaSinaisVitais(List<MamObrigatoriedade> mamObrigatoriedades) throws ApplicationBusinessException {

		// Cria lista com os ids para chamada do serviço #35290
		List<Short> listId = this.montarListaIdsObrigatoriedades(mamObrigatoriedades);

		// Chama o serviço #35290 para buscar os dados dos sinais vitais
		List<DadosSinaisVitaisVO> dadosSinaisVitais = null;
		try {
			dadosSinaisVitais = controlePacienteService.pesquisarDadosSinaisVitais(listId);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamItemSinalVitalRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(MamItemSinalVitalRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL);
		}
		return dadosSinaisVitais;
	}

	/**
	 * Busca o item de sital vital obrigatório à partir de uma obrigatoriedade
	 * 
	 * RN01 de #32658
	 * 
	 * @param mamObrigatoriedades
	 * @param itensObrigatoriosOrigem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ItemObrigatorioVO buscarItemSinaisVitais(MamObrigatoriedade mamObrigatoriedade, DadosSinaisVitaisVO dadosSinaisVitaisVO)
			throws ApplicationBusinessException {
		if (mamObrigatoriedade.getIceSeq() != null) {
			ItemObrigatorioVO itemObrigatorioVO = new ItemObrigatorioVO(mamObrigatoriedade.getSeq(), mamObrigatoriedade.getIndSituacao().isAtivo());
			if (dadosSinaisVitaisVO != null) {
				itemObrigatorioVO.setSeqItem(Integer.valueOf(dadosSinaisVitaisVO.getSeq()));
				itemObrigatorioVO.setDescricao(dadosSinaisVitaisVO.getDescricao());
				itemObrigatorioVO.setSigla(dadosSinaisVitaisVO.getSigla());
			} else {
				itemObrigatorioVO.setDescricao(super.getResourceBundleValue("MENSAGEM_REGISTRO_SINAIS_VITAIS_NAO_LOCALIZADO"));
			}
			return itemObrigatorioVO;
		}
		return null;
	}

	/**
	 * Cria uma lista com os ids das obrigatoriedades
	 * 
	 * @param mamObrigatoriedades
	 */
	private List<Short> montarListaIdsObrigatoriedades(List<MamObrigatoriedade> mamObrigatoriedades) {
		List<Short> listId = new ArrayList<Short>();
		for (MamObrigatoriedade mamObrigatoriedade : mamObrigatoriedades) {
			if (mamObrigatoriedade.getIceSeq() != null) {
				listId.add(mamObrigatoriedade.getIceSeq());
			}
		}
		return listId;
	}

	/**
	 * Busca os sinais vitais para popular a suggestion
	 * 
	 * C2 de #32658
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemObrigatorioVO> pesquisarSinaisVitais(String parametro) throws ApplicationBusinessException {
		List<ItemObrigatorioVO> result = null;

		Integer seqGrupo = this.buscarParametroGrupoControleSinaisVitais();

		List<DadosSinaisVitaisVO> dadosSinaisVitais = controlePacienteService
				.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(parametro, seqGrupo, 100);

		result = new ArrayList<ItemObrigatorioVO>();

		for (DadosSinaisVitaisVO dadosSinaisVitaisVO : dadosSinaisVitais) {
			ItemObrigatorioVO item = new ItemObrigatorioVO();
			item.setSeqItem(Integer.valueOf(dadosSinaisVitaisVO.getSeq()));
			item.setDescricao(dadosSinaisVitaisVO.getDescricao());
			item.setSigla(dadosSinaisVitaisVO.getSigla());
			result.add(item);
		}

		return result;
	}

	/**
	 * Busca o número de sinais vitais para popular a suggestion
	 * 
	 * C2 de #32658
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarSinaisVitaisCount(String parametro) throws ApplicationBusinessException {
		try {
			Integer seqGrupo = this.buscarParametroGrupoControleSinaisVitais();
			return controlePacienteService.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(parametro, seqGrupo);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(MamItemSinalVitalRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL);
		}
	}

	/**
	 * Busca o valor numérico do parâmetro P_GRUPO_CONTROLE_SINAIS_VITAIS, através de chamada do serviço #34780
	 * 
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private Integer buscarParametroGrupoControleSinaisVitais() throws ApplicationBusinessException {
		Object parametroGrupo = parametroFacade.obterAghParametroPorNome("P_GRUPO_CONTROLE_SINAIS_VITAIS", "vlrNumerico");
		Integer seqGrupo = null;
		if (parametroGrupo != null) {
			seqGrupo = ((BigDecimal) parametroGrupo).intValue();
		}
		return seqGrupo;
	}

	/**
	 * Monta um registro de MamObrigatoriedade, executando trigger de pos-update
	 * 
	 * RN02 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public MamObrigatoriedade montarRegistroSinalVital(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = new MamObrigatoriedade();

		// 1. Setar o SEQ do registro selecionado no item 2 do quadro descritivo no campo MAM_OBRIGATORIEDADES.ICE_SEQ
		mamObrigatoriedade.setIceSeq(itemObrigatorioVO.getSeqItem().shortValue());
		mamObrigatoriedade.setIndSituacao(DominioSituacao.getInstance(itemObrigatorioVO.getIndSituacaoObr()));

		// 2. Setar o SEQ de MAM_DESCRITORES do fieldset “Descritores” no campo MAM_OBRIGATORIEDADES.DCT_SEQ
		MamDescritor descritor = mamDescritorDAO.obterPorChavePrimaria(seqDesc);
		mamObrigatoriedade.setMamDescritor(descritor);

		// 3. Se o registro já tiver sido cadastrado apresentar mensagem “MENSAGEM_SINAL_VITAL_JA_EXISTENTE”
		// Verificar se existe algum MAM_OBRIGATORIEDADES da mesma MAM_DESCRITORES que já tenha o mesmo ICE_SEQ
		boolean existsItem = mamObrigatoriedadeDAO.existsItemSinalVitalPorDescritor(mamObrigatoriedade.getIceSeq(), descritor);
		if (existsItem) {
			throw new ApplicationBusinessException(MamItemSinalVitalRNExceptionCode.MENSAGEM_SINAL_VITAL_JA_EXISTENTE);
		}

		return mamObrigatoriedade;
	}
}
