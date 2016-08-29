package br.gov.mec.aghu.exameselaudos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.vo.NumeroApTipoVO;
import br.gov.mec.aghu.exameselaudos.BuscarResultadosExamesVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AghParametros;

@Stateless
public class ExamesLaudosON extends BaseBusiness {


@EJB
private ExamesLaudosRN examesLaudosRN;

private static final Log LOG = LogFactory.getLog(ExamesLaudosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelResultadoExameDAO aelResultadoExameDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1703963933487913288L;

	public DominioOrigemAtendimento buscaLaudoOrigemPaciente(Integer soeSeq) {
		return getExamesLaudosRN().buscaLaudoOrigemPaciente(soeSeq);
	}

	public String buscaJustificativaLaudo(Integer seqAtendimento, Integer phiSeq) {
		return getExamesLaudosRN().buscaJustificativaLaudo(seqAtendimento,
				phiSeq);
	}

	public List<BuscarResultadosExamesVO> buscarResultadosExames(
			Integer pacCodigo, Date dataLiberacao, String nomeCampo)
			throws ApplicationBusinessException {
		List<BuscarResultadosExamesVO> listaVO = null;

		List<AelResultadoExame> resultadosExames = getAelResultadoExameDAO()
				.buscarResultadosExames(pacCodigo, dataLiberacao, nomeCampo);

		if (resultadosExames != null && !resultadosExames.isEmpty()) {
			String situacaoItemSolicitacaoCodigo = null;

			AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			if (aghParametros != null) {
				situacaoItemSolicitacaoCodigo = aghParametros.getVlrTexto();
			}

			listaVO = new ArrayList<BuscarResultadosExamesVO>(resultadosExames
					.size());

			for (AelResultadoExame resultadoExame : resultadosExames) {
				BuscarResultadosExamesVO vo = new BuscarResultadosExamesVO();

				BigDecimal resultado = new BigDecimal(resultadoExame.getValor());
				
				if(resultadoExame.getParametroCampoLaudo() != null && resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais() != null){
					resultado = resultado.divide(new BigDecimal(Math.pow(10, resultadoExame.getParametroCampoLaudo()
								.getQuantidadeCasasDecimais())));
				}

				vo.setData(getExamesLaudosRN().buscaMaiorDataRecebimento(resultadoExame.getItemSolicitacaoExame().getId().getSoeSeq(),
						resultadoExame.getItemSolicitacaoExame().getId().getSeqp(), situacaoItemSolicitacaoCodigo));
				vo.setResultado(resultado);

				listaVO.add(vo);
			}
		}

		return listaVO;
	}
	
	/**
	 * @author twickert
	 * 
	 * Verifica se determinada solicitacao/item se refere a um laudo unico da patologia
	 * 
	 * @param mapSolicitacoes
	 */
public void verificaLaudoPatologia(Map<Integer, Vector<Short>> mapSolicitacoes, Boolean isHist) {
		
		//chave solicitacao;itemSolicitacao
		List<String> lista = new ArrayList<String>();
		
		AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO = getAelItemSolicitacaoExameDAO();
		
		List<Integer> removeSolicList = new ArrayList<Integer>();
		if(mapSolicitacoes != null && !mapSolicitacoes.isEmpty()) {
			for (Map.Entry<Integer, Vector<Short>> solicitacaoes : mapSolicitacoes.entrySet()) {
				
				Integer solicitacao = solicitacaoes.getKey();
				Vector<Short> seqps = solicitacaoes.getValue();
				
				List<Short> removeSeqList = new ArrayList<Short>();
				for (Short seqp : seqps) {
					NumeroApTipoVO numeroApTipoVOquery = aelItemSolicitacaoExameDAO.obterNumeroApTipoPorSolicitacao(solicitacao, seqp, isHist);
					
					if (numeroApTipoVOquery != null) {
						String key = numeroApTipoVOquery.getNumeroAp() + "_" + numeroApTipoVOquery.getLu2Seq();
						if (lista.contains(key)) {
							removeSeqList.add(seqp);
						}
						else {
							lista.add(key);
						}					
					}
				}
				
				for (Short remove : removeSeqList) {
					seqps.remove(remove);
				}
				
				if (seqps.isEmpty()) {
					removeSolicList.add(solicitacao);
				}
			}
		}
		for (Integer remove : removeSolicList) {
			mapSolicitacoes.remove(remove);
		}
	}
	
	/**
	 * @author twickert
	 * 
	 * Verifica se determinada solicitacao/item se refere a um laudo unico da patologia
	 * 
	 * @param mapSolicitacoes
	 */
	public void verificaLaudoPatologia(List<AelItemSolicitacaoExames> listaSolicitacoes, Boolean isHist) {
		
		List<String> lista = new ArrayList<String>();
		List<AelItemSolicitacaoExames> removeList = new ArrayList<AelItemSolicitacaoExames>();
		
		AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO = getAelItemSolicitacaoExameDAO();
		
		for (AelItemSolicitacaoExames itemSolic : listaSolicitacoes) {
			
			NumeroApTipoVO numeroApTipoVOquery = aelItemSolicitacaoExameDAO.obterNumeroApTipoPorSolicitacao(itemSolic.getId().getSoeSeq(), itemSolic.getId().getSeqp(), isHist);
			
			if (numeroApTipoVOquery != null) {				
				String key = numeroApTipoVOquery.getNumeroAp() + "_" + numeroApTipoVOquery.getLu2Seq();
				
				if (lista.contains(key)) {
					removeList.add(itemSolic);
				}
				else {
					lista.add(key);
				}					
			}
		}
		
		for (AelItemSolicitacaoExames itemSolic : removeList) {
			listaSolicitacoes.remove(itemSolic);
		}
		
	}

	protected ExamesLaudosRN getExamesLaudosRN() {
		return examesLaudosRN;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
}
