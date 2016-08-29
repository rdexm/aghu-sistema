package br.gov.mec.aghu.prescricaomedica.business;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;

/**
 * Responsavel por identificar quais itens estao gravados / associados e devem ser marcados com check.<br>
 * E gerar lista de Itens Mesclados entre <b>MpmAltaRecomendacao</b> e <b>ItemPrescricaoMedica</b><br>
 * Trata apenas Dietas, Cuidados e Medicamentos.<br>
 * 
 * @author rcorvalao
 */
class RecomendacaoAltaItensPrescricaoHelper {
	
	private Map<PrescricaoMedicaTypes, List<String>> mapRecomendacoesAssociadas;
	private Map<String, MpmAltaRecomendacao> mapEntidadeRecomendacoesAssociadas;
	private List<ItemPrescricaoMedicaVO> itensPrescricaoMedicaMesclados;
	
	/**
	 * Instancia o Helper com os valores associados ao AltaSumario.
	 * @param listAltaRecomendacao
	 * @throws IllegalArgumentException
	 */
	public RecomendacaoAltaItensPrescricaoHelper(List<MpmAltaRecomendacao> listAltaRecomendacao) {
		if (listAltaRecomendacao == null) {
			throw new IllegalArgumentException("Lista nao pode ser Nula.");
		}
		for (MpmAltaRecomendacao altaRecomendacao : listAltaRecomendacao) {
			String key = null;
			PrescricaoMedicaTypes tipo = null;
			
			if (altaRecomendacao.getPcuAtdSeq() != null) {
				key = this.getItemPrescricaoKey(altaRecomendacao.getPcuAtdSeq(), altaRecomendacao.getPcuSeq());
				tipo = PrescricaoMedicaTypes.CUIDADOS_MEDICOS;
			}
			if (altaRecomendacao.getPdtAtdSeq() != null) {
				key = this.getItemPrescricaoKey(altaRecomendacao.getPdtAtdSeq(), altaRecomendacao.getPdtSeq());
				tipo = PrescricaoMedicaTypes.DIETA;
			}
			if (altaRecomendacao.getPmdAtdSeq() != null) {
				key = this.getItemPrescricaoKey(altaRecomendacao.getPmdAtdSeq(), altaRecomendacao.getPmdSeq());
				tipo = PrescricaoMedicaTypes.MEDICAMENTO;
			}
			
			if (key != null) {
				List<String> listKey;
				if (this.getMapRecomendacoesAssociadas().containsKey(tipo)) {
					listKey = this.getMapRecomendacoesAssociadas().get(tipo);
				} else {
					listKey = new LinkedList<String>();
					this.getMapRecomendacoesAssociadas().put(tipo, listKey);
				}
				listKey.add(key);
				this.getMapEntidadeRecomendacoesAssociadas().put(key, altaRecomendacao);
			}
		}//FOR
	}
	
	/**
	 * Retorna a lista que foi Construida neste Helper.<br>
	 * Deve ser chamado depois do metodo <b>construirListar</b>.<br>
	 * 
	 * @return List of <code>ItemPrescricaoMedicaVO</code>
	 * @throws IllegalStateException
	 */
	public List<ItemPrescricaoMedicaVO> getResultado() {
		if (this.itensPrescricaoMedicaMesclados == null) {
			throw new IllegalStateException("Possivelmente o metodo construirListar ainda nao foi executado!");
		}
		return this.itensPrescricaoMedicaMesclados;
	}
	
	private String getItemPrescricaoKey(Integer pcuAtdSeq, Integer pcuSeq) {
		if (pcuAtdSeq == null || pcuSeq == null) {
			throw new IllegalArgumentException("Parametros obrigatorios nao informados!!");
		}
		
		return pcuAtdSeq + "|" + pcuSeq;
	}
	
	/**
	 * Cria um Item de Prescricao Medica para exibicao na lista. 
	 * Baseado na Alta Recomendacao.<br>
	 * 
	 * @param altaRecomendacao
	 * @return
	 */
	public ItemPrescricaoMedicaVO newItenPrescricaoMedicaVO(MpmAltaRecomendacao altaRecomendacao) {
		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		
		item.setDescricao(altaRecomendacao.getDescricao());
		item.setMesclado(Boolean.TRUE);
		item.setMarcado(Boolean.TRUE);
		
		item.setSeqp(altaRecomendacao.getId().getSeqp());
		if (altaRecomendacao.getPcuAtdSeq() != null) {
			item.setAtendimentoSeq(altaRecomendacao.getPcuAtdSeq());
			item.setItemSeq(Long.valueOf(altaRecomendacao.getPcuSeq()));
			item.setTipo(PrescricaoMedicaTypes.CUIDADOS_MEDICOS);
		}

		if (altaRecomendacao.getPdtAtdSeq() != null) {
			item.setAtendimentoSeq(altaRecomendacao.getPdtAtdSeq());
			item.setItemSeq(Long.valueOf(altaRecomendacao.getPdtSeq()));
			item.setTipo(PrescricaoMedicaTypes.DIETA);
		}
		
		if (altaRecomendacao.getPmdAtdSeq() != null) {
			item.setAtendimentoSeq(altaRecomendacao.getPmdAtdSeq());
			item.setItemSeq(Long.valueOf(altaRecomendacao.getPmdSeq()));
			item.setTipo(PrescricaoMedicaTypes.MEDICAMENTO);
		}
		
		return item;
	}

	/**
	 * @return the listaRecomendacoesAssociadas
	 */
	protected Map<PrescricaoMedicaTypes, List<String>> getMapRecomendacoesAssociadas() {
		if (this.mapRecomendacoesAssociadas == null) {
			this.mapRecomendacoesAssociadas = new HashMap<PrescricaoMedicaTypes, List<String>>();
		}
		return this.mapRecomendacoesAssociadas;
	}

	protected Map<String, MpmAltaRecomendacao> getMapEntidadeRecomendacoesAssociadas() {
		if (this.mapEntidadeRecomendacoesAssociadas == null) {
			this.mapEntidadeRecomendacoesAssociadas = new HashMap<String, MpmAltaRecomendacao>();
		}
		return mapEntidadeRecomendacoesAssociadas;
	}
	
}
