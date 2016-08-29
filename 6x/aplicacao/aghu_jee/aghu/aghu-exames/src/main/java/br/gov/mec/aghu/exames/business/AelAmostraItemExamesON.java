package br.gov.mec.aghu.exames.business;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.vo.AelAmostraItemExamesVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * 
 * @author lsamberg
 *
 */
@Stateless
public class AelAmostraItemExamesON extends BaseBusiness {


@EJB
private GerarProtocoloUnicoRN gerarProtocoloUnicoRN;

@EJB
private AelAmostraItemExamesRN aelAmostraItemExamesRN;

private static final Log LOG = LogFactory.getLog(AelAmostraItemExamesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelAmostrasDAO aelAmostrasDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6728907832297521559L;

	public void persistirAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, Boolean flush, String nomeMicrocomputador) throws BaseException{
		if(aelAmostraItemExames.getId() != null){
			atualizarAelAmostraItemExames(aelAmostraItemExames, flush, true, nomeMicrocomputador);		
		}
		//TODO: IMPLEMENTAR INSERT
	}
	

	private void atualizarAelAmostraItemExames(AelAmostraItemExames aelAmostraItemExames, Boolean flush, Boolean atualizaItemSolic, String nomeMicrocomputador) throws BaseException{
		getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(aelAmostraItemExames, flush, atualizaItemSolic, nomeMicrocomputador);
	}


	public List<AelAmostraItemExamesVO> pesquisarAmostraItemExames(AghUnidadesFuncionais unidadeExecutora,
			Integer solicitacao, Integer amostra, AelEquipamentos equipamento, String sigla, DominioSimNao enviado,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
				
		List<Short> aghUnfSeqList = this.obterAghUnfSeqList(unidadeExecutora);
		
		List<AelAmostraItemExamesVO> resultList = this.getAelAmostraItemExamesDAO()
			.pesquisarAmostraItemExames(solicitacao, amostra, equipamento, sigla, aghUnfSeqList, 
					enviado, unidadeExecutora.getSeq(), firstResult, maxResult, orderProperty, asc);
		
		return resultList;
	}
	

	public Long pesquisarAmostraItemExamesCount(AghUnidadesFuncionais unidadeExecutora,
			Integer solicitacao, Integer amostra, AelEquipamentos equipamento, String sigla, DominioSimNao enviado) {
		
		List<Short> aghUnfSeqList = this.obterAghUnfSeqList(unidadeExecutora);
		
		return this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamesCount(solicitacao, amostra, equipamento, 
				sigla, unidadeExecutora.getSeq(), aghUnfSeqList, enviado);
	}
	
	
	
	private List<Short> obterAghUnfSeqList(AghUnidadesFuncionais unidadeExecutora) {
		
		List<Short> listUnfSeqHierarquico = this.getAghuFacade()
		.obterUnidadesFuncionaisHierarquicasPorCaractCentralRecebimento(unidadeExecutora.getSeq());
		
		if(listUnfSeqHierarquico == null || listUnfSeqHierarquico.isEmpty()) {
			listUnfSeqHierarquico = new LinkedList<Short>();
			listUnfSeqHierarquico.add(Short.valueOf("0"));
		}
		
		return listUnfSeqHierarquico;
	}
		
	
	public Boolean realizarCargaInterfaceamento(Set<AelAmostraItemExamesVO> listAmostraItemExamesVO, String nomeMicrocomputador) throws BaseException {
		final String modoInterfaceamento = this.getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_MODO_INTERFACEAMENTO).getVlrTexto();
		
		if(modoInterfaceamento.equals("H") || modoInterfaceamento.equals("N")) {
			for (AelAmostraItemExamesVO vo:  listAmostraItemExamesVO) {
				this.getGerarProtocoloUnicoRN().gerarProtocoloUnico(this.getAelAmostrasDAO()
						.obterAelAmostras(vo.getAmoSoeSeq(), vo.getAmoSeqp()), Boolean.FALSE, vo.getExame());
				final AelAmostraItemExames elemento = this.getAelAmostraItemExamesDAO()
						.obterPorChavePrimaria(vo.getId().getIseSoeSeq(), vo.getId().getIseSeqp(), vo.getId().getAmoSoeSeq(), vo.getId().getAmoSeqp());
				elemento.setIndEnviado(Boolean.TRUE);
				this.getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(elemento, Boolean.FALSE, Boolean.FALSE, nomeMicrocomputador);
			}
			//exibir mensagem de confirmação.
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	
	
	public List<AelAmostraItemExamesVO> listarAmostraItemExamesTodos(AghUnidadesFuncionais unidadeExecutora,
			Integer solicitacao, Integer amostra, AelEquipamentos equipamento, String sigla, DominioSimNao enviado) {
		
		List<AelAmostraItemExamesVO> resultList = this.getAelAmostraItemExamesDAO()
			.pesquisarAmostraItemExamesTodos(solicitacao, amostra, equipamento, sigla, enviado, 
					this.obterAghUnfSeqList(unidadeExecutora), unidadeExecutora.getSeq());
		
		for (AelAmostraItemExamesVO vo : resultList) {
			vo.setSelecionado(Boolean.TRUE);
		}
		
		return resultList;
	}
	
	
	
	
	/** GET/SET **/
	protected AelAmostraItemExamesRN getAelAmostraItemExamesRN() {
		return aelAmostraItemExamesRN;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	
	
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected GerarProtocoloUnicoRN getGerarProtocoloUnicoRN() {
		return gerarProtocoloUnicoRN;
	}
}
