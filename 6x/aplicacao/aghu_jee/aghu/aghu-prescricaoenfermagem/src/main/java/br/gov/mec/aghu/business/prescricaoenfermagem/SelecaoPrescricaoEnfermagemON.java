package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidadosId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * @author tfelini
 */
@Stateless
public class SelecaoPrescricaoEnfermagemON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SelecaoPrescricaoEnfermagemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	private static final long serialVersionUID = -1893272696881303934L;
	
	/**
	 * @ORADB PROCEDURE FORMS EPEP_POPULA_SINTAXE
	 */
	public List<ItemPrescricaoEnfermagemVO> buscarItensPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId, Boolean listarTodas)
			throws ApplicationBusinessException {
		if (prescricaoEnfermagemId == null || prescricaoEnfermagemId.getAtdSeq() == null || prescricaoEnfermagemId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscarPrescricaoEnfermagem: parametros de filtro invalido");
		}
		
		PrescricaoEnfermagemVO prescricaoEnfermagemVO = new PrescricaoEnfermagemVO();
		
		// Busca dos Itens
		this.populaCuidadoEnfermagem(prescricaoEnfermagemVO, prescricaoEnfermagemId, listarTodas);

		return prescricaoEnfermagemVO.getItens();
	}
	
	/**
	 * @ORADB PROCEDURE FORMS EPEP_POPULA_CUIDADO
	 */
	private void populaCuidadoEnfermagem(PrescricaoEnfermagemVO prescricaoEnfermagemVO, EpePrescricaoEnfermagemId prescricaoEnfermagemId, Boolean listarTodas) {
		EpePrescricaoEnfermagem prescricaoEnfermagem = getEpePrescricaoEnfermagemDAO().obterPorChavePrimaria(prescricaoEnfermagemId);

		List<EpePrescricoesCuidados> listaCuidadosEnfermagem = getEpePrescricoesCuidadosDAO().pesquisarCuidadosPrescricao(
				prescricaoEnfermagem.getId(), prescricaoEnfermagem.getDthrFim(), listarTodas, false);
		
		List<EpePrescricoesCuidados> listaCuidadosEnfermagemOriginal = (List<EpePrescricoesCuidados>)(new ArrayList<EpePrescricoesCuidados>(listaCuidadosEnfermagem)).clone();

		if(listarTodas) {
			CollectionUtils.filter(listaCuidadosEnfermagem, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((EpePrescricoesCuidados)o).getPrescricaoCuidado() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}

		this.ordenarListaDeItemPrescricaoEnfermagem(listaCuidadosEnfermagem);
		for (EpePrescricoesCuidados cuidado : listaCuidadosEnfermagem) {
			this.verificaHierarquiaCuidados(prescricaoEnfermagemVO, cuidado, listaCuidadosEnfermagemOriginal, false);
		}
	}
	
	private void verificaHierarquiaCuidados(PrescricaoEnfermagemVO prescricaoEnfermagemVO, EpePrescricoesCuidados cuidado, List<EpePrescricoesCuidados> listaCompleta, Boolean isHierarquico) {
		
		final EpePrescricoesCuidadosId id = new EpePrescricoesCuidadosId(cuidado.getId().getAtdSeq(), cuidado.getId().getSeq());
		listaCompleta.remove(cuidado);

		ItemPrescricaoEnfermagemVO item = new ItemPrescricaoEnfermagemVO();
		item.setDescricao(cuidado.getDescricaoFormatada());
		item.setCriadoEm(cuidado.getCriadoEm());
		item.setAlteradoEm(cuidado.getAlteradoEm());
		item.setDthrInicio(cuidado.getDthrInicio());
		item.setDthrFim(cuidado.getDthrFim());									
		item.setSituacao(cuidado.getPendente());
		item.setServidorValidacao(cuidado.getServidorValida());
		item.setServidorValidaMovimentacao(cuidado.getServidorMvtoValida());
		item.setAtendimentoSeq(cuidado.getId().getAtdSeq());
		item.setItemSeq(cuidado.getId().getSeq());
		item.setTipo(PrescricaoMedicaTypes.CUIDADOS_MEDICOS);
		item.setHierarquico(isHierarquico);
		prescricaoEnfermagemVO.addItem(item);

		EpePrescricoesCuidados prescricaoAlterada = (EpePrescricoesCuidados)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((EpePrescricoesCuidados)o).getPrescricaoCuidado() != null && ((EpePrescricoesCuidados)o).getPrescricaoCuidado().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaCuidados(prescricaoEnfermagemVO, prescricaoAlterada, listaCompleta, true);	
		}
	}
	
	/*
	 * Orderna lista pela descricaoFormatada
	 * 
	 * @param list
	 */
	private void ordenarListaDeItemPrescricaoEnfermagem(List<? extends EpePrescricoesCuidados> list) {
		Collections.sort(list, new Comparator<EpePrescricoesCuidados>() {
			@Override
			public int compare(EpePrescricoesCuidados item1, EpePrescricoesCuidados item2) {
				return item1.getDescricaoFormatada().compareTo(item2.getDescricaoFormatada());
			}
		});
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO(){
		return epePrescricoesCuidadosDAO;
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO(){
		return epePrescricaoEnfermagemDAO;
	}
}
