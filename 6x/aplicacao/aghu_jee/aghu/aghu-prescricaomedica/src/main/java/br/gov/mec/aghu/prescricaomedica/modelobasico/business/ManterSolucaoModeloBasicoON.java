package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterSolucaoModeloBasicoON extends BaseBusiness {

@EJB
private ManterModeloBasicoMedicamentoRN manterModeloBasicoMedicamentoRN;

@EJB
private ManterItemModeloBasicoMedicamentoRN manterItemModeloBasicoMedicamentoRN;

private static final Log LOG = LogFactory.getLog(ManterSolucaoModeloBasicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;


@Inject
private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -8147151221023118069L;

	/**
	 * Insere ou Atualiza uma Solucao do Modelo Basico
	 * 
	 * @param solucao
	 * @param listaExcluidos
	 * @return mensagem de sucesso
	 * @throws ApplicationBusinessException
	 */
	public String gravarSolucao(MpmModeloBasicoMedicamento solucao, List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento, List<MpmItemModeloBasicoMedicamento> listaExcluidos) throws ApplicationBusinessException {
		String returnMessage = null;
		if (solucao.getId() == null) {
			this.inserirSolucao(solucao, itensModeloMedicamento);
			returnMessage = "MENSAGEM_SUCESSO_INCLUSAO_SOLUCAO_MODELO";
		} else {
			this.atualizarSolucao(solucao, itensModeloMedicamento, listaExcluidos);
			returnMessage = "MENSAGEM_SUCESSO_ALTERACAO_SOLUCAO_MODELO";
		}
		return returnMessage;
	}

	private void inserirSolucao(MpmModeloBasicoMedicamento solucao, List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento) throws ApplicationBusinessException {
		ManterModeloBasicoMedicamentoRN umModBasMedicamentoRN = this.getManterModeloBasicoMedicamentoRN();

		//		List<MpmItemModeloBasicoMedicamento> itens = solucao.getItensModeloMedicamento();
		//		solucao.setItensModeloMedicamento(null);

		umModBasMedicamentoRN.inserirModeloBasicoMedicamento(solucao);

		ManterItemModeloBasicoMedicamentoRN itemRN = this.getManterItemModeloBasicoMedicamentoRN();
		for (MpmItemModeloBasicoMedicamento itemModBasMedicamento : itensModeloMedicamento) {
			itemModBasMedicamento.setModeloBasicoMedicamento(solucao);
			itemModBasMedicamento.setServidor(solucao.getServidor());
			itemRN.inserirItemModeloBasicoMedicamento(itemModBasMedicamento);
		}
		//		this.getMpmModeloBasicoMedicamentoDAO().refresh(solucao);
	}

	private ManterItemModeloBasicoMedicamentoRN getManterItemModeloBasicoMedicamentoRN() {
		return manterItemModeloBasicoMedicamentoRN;
	}

	private ManterModeloBasicoMedicamentoRN getManterModeloBasicoMedicamentoRN() {
		return manterModeloBasicoMedicamentoRN;
	}

	private void atualizarSolucao(MpmModeloBasicoMedicamento solucao, List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento, List<MpmItemModeloBasicoMedicamento> listaExcluidos) throws ApplicationBusinessException {
		// Itens para Inclusao e Alteracao.
		for (MpmItemModeloBasicoMedicamento item : itensModeloMedicamento) {
			MpmItemModeloBasicoMedicamento itemEditado = null;
			if(item.getId() != null){
				itemEditado = getMpmItemModeloBasicoMedicamentoDAO().obterPorChavePrimaria(item.getId());
			}
			if (itemEditado == null) {
				this.getManterItemModeloBasicoMedicamentoRN().inserirItemModeloBasicoMedicamento(item);				
			} else {
				this.getManterItemModeloBasicoMedicamentoRN().atualizarItemModeloBasicoMedicamento(item);
			}
		}

		// Itens para Exclusao.
		// Devido a regra de negocio: que nao deixa um Medicamento/Solucao ficar sem nenhum Item, 
		// as exclusoes deve ser feita depois das Inclusoes/Alteracoes.
		for (MpmItemModeloBasicoMedicamento itemModBasSolucao : listaExcluidos) {
			this.getManterItemModeloBasicoMedicamentoRN().removerItemModeloBasicoMedicamento(itemModBasSolucao);
		}

		// Atualizar o Medicamento do Modelo Basico.	
		this.getManterModeloBasicoMedicamentoRN().atualizarModeloBasicoMedicamento(solucao);
	}

	/**
	 * Remove os itens selecionados.<br>
	 * Se nenhum item estiver selecioando para excluao corre um erro.
	 * 
	 * @param listaSolucoesDoModeloBasico
	 * @throws ApplicationBusinessException
	 */
	public void removerSolucoesSelecionadas(MpmModeloBasicoMedicamento modeloBasicoMedicamento) throws ApplicationBusinessException {
		if (modeloBasicoMedicamento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		modeloBasicoMedicamento =  getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(modeloBasicoMedicamento.getId());
		//boolean removeu = false;
		//	for (ModeloBasicoMedicamentoVO modBasSolucaoVo : listaSolucoesDoModeloBasico) {
		if (modeloBasicoMedicamento != null) {
			this.getManterModeloBasicoMedicamentoRN().removerModeloBasicoMedicamento(modeloBasicoMedicamento);
		}

	}

	private MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}
	
	private MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}
	

	/**
	 * Retorna Modelo Basico de Medicamento pelo id.<br>
	 * Faz refresh.
	 * 
	 * @param seqModelo
	 * @param seqItemModelo
	 * @return
	 */
	public MpmModeloBasicoMedicamento obterModeloBasicoSolucao(Integer seqModelo, Integer seqItemModelo) {
		MpmModeloBasicoMedicamento entity = this.getMpmModeloBasicoMedicamentoDAO().obterModeloBasicoSolucao(seqModelo, seqItemModelo);
		return entity;
	}


}
