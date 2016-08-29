package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelDocResultadoExamesHist;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadosExamesHist;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesHistoricoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVOComparator;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultarExamesHistON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConsultarExamesHistON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IExamesLaudosFacade examesLaudosFacade;

//@EJB
//private IPacienteFacade pacienteFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

	private static final long serialVersionUID = -4311047974043695557L;

	public enum ConsultarExamesPolONExceptionCode implements BusinessExceptionCode {
		PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO;
	}

	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacao(
			Integer codigoPaciente,
			Short unfSeq,
			Date data,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame, Integer gmaSeq) throws ApplicationBusinessException {
		// Busca dos Itens de Atendimento e AtendimetoDiverso

		List<AelGrupoXMaterialAnalise> gruposMateriaisAnalises = null;
		List<Integer> seqMateriaisAnalises = null;
		//caso seja uma pesquisa relacionada a grupo material de analise,
		//utiliza-se como filtro o seq do grupo
		if(gmaSeq!=null){
			//obtém todos os materiais análises que possuem o gmaSeq passado como parametro
			//pela relação many-to-many
			gruposMateriaisAnalises = getExamesFacade().pesquisarGrupoXMateriaisAnalisesPorGrupo(gmaSeq);
			if(gruposMateriaisAnalises!=null){
				seqMateriaisAnalises = new ArrayList<Integer>();
			}
			for(AelGrupoXMaterialAnalise grupoMaterialAnalise : gruposMateriaisAnalises){
				seqMateriaisAnalises.add(grupoMaterialAnalise.getId().getManSeq());
			}
		}
		Map<AelItemSolicExameHist, AelDocResultadoExamesHist> mapISEAnexos = new HashMap<AelItemSolicExameHist, AelDocResultadoExamesHist>();

		List<ExamesHistoricoPOLVO> listaAtdObj = getExamesFacade().buscaExamesPeloCodigoPacienteSituacaoAtendimentoHist(codigoPaciente, unfSeq, situacaoItemExame, seqMateriaisAnalises);
		List<AelItemSolicExameHist> listaAtd = new ArrayList<AelItemSolicExameHist>();
		adicionaISEHashLista(listaAtdObj, mapISEAnexos, listaAtd);

		List<ExamesHistoricoPOLVO> listaAtvObj =  getExamesFacade().buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiversoHist(codigoPaciente, unfSeq, situacaoItemExame, seqMateriaisAnalises);
		List<AelItemSolicExameHist> listaAtv = new ArrayList<AelItemSolicExameHist>();
		adicionaISEHashLista(listaAtvObj, mapISEAnexos, listaAtv);

		List<AelItemSolicExameHist> lista = new LinkedList<AelItemSolicExameHist>();
		lista.addAll(listaAtd);
		lista.addAll(listaAtv);

		List<ItemSolicitacaoExamePolVO> listaVO = new ArrayList<ItemSolicitacaoExamePolVO>();
		for (AelItemSolicExameHist ise : lista) {
			ItemSolicitacaoExamePolVO iseVO = new ItemSolicitacaoExamePolVO();

			if (DominioSituacaoItemSolicitacaoExame.LI.equals(situacaoItemExame)) {
				iseVO.setDthrLiberada(ise.getDataHoraEventomaxExtratoItem());
			} else {
				iseVO.setDthrLiberada(ise.getSolicitacaoExame().getCriadoEm());
			}

			boolean filtroData = true;
			if (data != null) {

				filtroData = false;
				Date dataComparacao1 = DateUtil.truncaData(data);
				Date dataComparacao2 = DateUtil.truncaData(iseVO.getDthrLiberada());

				if (DateUtil.isDatasIguais(dataComparacao1, dataComparacao2)) {
					filtroData = true;
				}

			}

			if (filtroData) {
				iseVO.setSeqp(ise.getId().getSeqp());
				iseVO.setExaDescricaoUsual(ise.getExame().getDescricaoUsual());
				
				if(processaSeDescricaoMatAnaliseEmBranco(ise.getSeqMatAnalise())){
					iseVO.setManDescricao("");
				}else{
					iseVO.setManDescricao(ise.getDescricaoGrupoMatAnalise());
				}
				
				iseVO.setSituacaoCodigoDescricao(ise.getSituacaoItemSolicitacao()
						.getDescricao());
				iseVO.setSoeSeq(ise.getSolicitacaoExame().getSeq());
				iseVO.setUnfDescricao(ise.getUnidadeFuncional().getDescricao());
				
				//Preenche coluna de resultado
				//preencheResultadoExame(ise, iseVO);
				
				iseVO.setResultado(buscaResultado(ise));

				//Busca o resultado dos anexos do map.
				iseVO.setTemAnexo((mapISEAnexos.containsKey(ise) && mapISEAnexos.get(ise) != null) ? Boolean.TRUE : Boolean.FALSE);
				
				iseVO.setNotasAdicionais(ise.getQtdeNotaAdicional() != 0);
				
				listaVO.add(iseVO);
			}	
			
			
		}

		Collections.sort(listaVO, new ItemSolicitacaoExamePolVOComparator());
		
		
		return listaVO;
	}

	/**
	 * AIPC_MONTA_AMOSTRA
	 * @param seqMatAnalsise
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean processaSeDescricaoMatAnaliseEmBranco(Integer seqMatAnalsise) throws ApplicationBusinessException {
		
		AghParametros codigosComDescricaoNula = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATERIAS_ANALISE_EXAMES_POL);
		StringTokenizer cods = new StringTokenizer(codigosComDescricaoNula.getVlrTexto(), ",");
		while(cods.hasMoreElements()){
			if(seqMatAnalsise.equals(Integer.valueOf(cods.nextElement().toString()))){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	
	/**
	 * @ORADB BUSCA_RESULTADO
	 * 
	 * @param ise
	 * @return
	 * @author bruno.mourao
	 *  
	 * @throws ApplicationBusinessException 
	 * @since 02/04/2012
	 */
	public String buscaResultado(AelItemSolicExameHist ise) throws ApplicationBusinessException {
		
		//TODO Verificar se não é administrador
		
		String result = null;
		
		Object resultadoExame = obtemPrimeiroResultadoExame(ise);
		
		if(resultadoExame != null && resultadoExame.toString().length() <= 10){
			result = resultadoExame.toString();
		}
		
		if(StringUtils.isNotBlank(result)){
			return formataResultado(result);
			
		}
		return result;
		
	}

	private String formataResultado(String result) {
		result = result.replace(',', '.');
		try{
			result = AghuNumberFormat.formatarValor(new BigDecimal(result)	, "########0.################");
			result = result.replace('.', ','); 
			return result;
		}catch (NumberFormatException e) {
			return result;
		}
	}

	/**
	 * Esta função retorna para uma solicitação o primeiro resultado do exame.
	 * @param ise
	 * @return
	 * @author bruno.mourao
	 * @since 02/04/2012
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private Object obtemPrimeiroResultadoExame(AelItemSolicExameHist ise){
		Object result = null;
		
		//Só faz alguma cois se possuir resultados
		if( ise.getResultadoExames() != null && !ise.getResultadoExames().isEmpty()){
			CoreUtil.ordenarLista(ise.getResultadoExames(), AelResultadosExamesHist.Fields.SEQP.toString(), false);
			
			//Conta os resultados indicados sem anulação de laudo
			int laudosNaoAnulados = 0;
			AelResultadosExamesHist primeiroResultado = null;
			for(AelResultadosExamesHist res : ise.getResultadoExames()){
				
				//Pega o primeiro exame
				if(primeiroResultado == null){
					primeiroResultado = res;
				}
				
				if(!res.getAnulacaoLaudo()){
					laudosNaoAnulados++;
				}

			}
			
			//Se tiver mais do que um, termina processamento
			if(laudosNaoAnulados > 1){
				return null;
			}
			
			//cursor c_resultado
			Double valor = null;
			if(primeiroResultado != null && primeiroResultado.getValor() != null &&  primeiroResultado.getParametroCampoLaudo() != null && primeiroResultado.getParametroCampoLaudo().getQuantidadeCasasDecimais() != null){
				valor = primeiroResultado.getValor() / Math.pow(10.0,primeiroResultado.getParametroCampoLaudo().getQuantidadeCasasDecimais().doubleValue());
			}
			
			//Se obteve o valor
			if(valor != null){
				return valor;
			}
			
			//Obtem resultado codificado
			AelResultadoCodificado resultadoCodificado = primeiroResultado.getResultadoCodificado();
			if(resultadoCodificado != null && resultadoCodificado.getDescricao() != null){
				//Se o tamanho do resultado for menor q 20
				if(resultadoCodificado.getDescricao().length() <=20){
					return resultadoCodificado.getDescricao();
				}
				else{
					//Maior que 20 = nulo
					return null;
				}
			}
			
			//obtem caracteristica 
			AelResultadoCaracteristica resultadoCaracteristica = primeiroResultado.getResultadoCaracteristica();
			if(resultadoCaracteristica != null && resultadoCaracteristica.getDescricao() != null){
				//se o tamanho da caracteristica for menor q 20
				if(resultadoCaracteristica.getDescricao().length() <= 20){
					return resultadoCaracteristica.getDescricao();
				}
				else{
					//Maior = nulo
					return null;
				}
				
			}
			
			//obtem descricao resultado
			if(primeiroResultado.getDescricao() != null){
				//se o tamanho da caracteristica for menor q 20
				if(primeiroResultado.getDescricao().length() <= 20){
					return primeiroResultado.getDescricao();
				}
				else{//Maior = nulo
					return null;
				}
			}
			
		}
		return result;
	}

	/**
	 * Adiciona em uma lista e em um map, á partir de um array de object, em que a primeira posição vem um objeto 
	 * AelItemSolicExameHist e na segunda
	 * vem um object do tipo AelDocResultadoExamesHist ou null.
	 * @param listaAtdObj
	 * @param mapISEAnexos
	 * @param lista
	 */
	private void adicionaISEHashLista(List<ExamesHistoricoPOLVO> listaAtdObj,
			Map<AelItemSolicExameHist, AelDocResultadoExamesHist> mapISEAnexos, List<AelItemSolicExameHist> lista) {
		if (listaAtdObj != null) {
			/**for (Object[] obj : listaAtdObj) {
			 * AelItemSolicExameHist aelISE = (AelItemSolicExameHist)obj[0]; //Primeira posição ISE
				aelISE.setDataHoraEventomaxExtratoItem((Date)obj[3]);
				aelISE.setQtdeNotaAdicional((Long)obj[4]);
			 */
			for(ExamesHistoricoPOLVO obj : listaAtdObj){
				AelItemSolicExameHist aelISE = obj.getAelItemSolicExameHist();
				aelISE.setDataHoraEventomaxExtratoItem(obj.getDataHoraEventomaxExtratoItem());
				aelISE.setQtdeNotaAdicional(obj.getQtdeNotaAdicional());
				aelISE.setDescricaoGrupoMatAnalise(obj.getDescricaoMatAnalise());//#25458
				aelISE.setSeqMatAnalise(obj.getSeqMatAnalise());//#25458
				//aelISE.setExame((AelExames)obj[5]);
				AelDocResultadoExamesHist aelDRE = null;
				List<AelDocResultadoExamesHist> docResultadoExameHist = getExamesFacade().obterDocumentoAnexadoHistPorItemSolicitacaoExameHist(obj.getAelItemSolicExameHist().getId().getSoeSeq(), obj.getAelItemSolicExameHist().getId().getSeqp());
				if (docResultadoExameHist != null && !docResultadoExameHist.isEmpty()) { //if (obj.getAelDocResultadoExamesHist() != null)
					aelDRE = docResultadoExameHist.get(0); //aelDRE = obj.getAelDocResultadoExamesHist(); //Primeira posição ISE
				}
				lista.add(aelISE);
				mapISEAnexos.put(aelISE, aelDRE);
			}
		}

	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadas(
			Integer codPaciente) throws ApplicationBusinessException {
		
		AghParametros paramSitCodLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros paramSitCodAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		if(paramSitCodLiberado != null && paramSitCodAreaExec != null){
		
			List<ExameAmostraColetadaVO> result = getExamesFacade().obterDadosPorAmostrasColetadasHist(codPaciente, paramSitCodLiberado.getVlrTexto(), paramSitCodAreaExec.getVlrTexto());
			
			//Ordena 
			Collections.sort(result, new Comparator<ExameAmostraColetadaVO>(){
				@Override
				public int compare(ExameAmostraColetadaVO arg0,
						ExameAmostraColetadaVO arg1) {
					
					int resultadoAtributoComparado;
					
					resultadoAtributoComparado = arg0.getOrdProntOnline().compareTo(arg1.getOrdProntOnline());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; }  
	               
	                resultadoAtributoComparado = arg0.getDescGrpMatAnalise().compareTo(arg1.getDescGrpMatAnalise());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; }                
	                
	                resultadoAtributoComparado = arg1.getDtExame().compareTo(arg0.getDtExame());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; } 
	                
	                resultadoAtributoComparado = arg0.getOrdemNivel1().compareTo(arg1.getOrdemNivel1());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; } 
	                
	                resultadoAtributoComparado = arg0.getOrdemNivel2().compareTo(arg1.getOrdemNivel2());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; } 
	                
	                resultadoAtributoComparado = arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual());  
	                if (resultadoAtributoComparado != 0){ return resultadoAtributoComparado; } 
	                
	                resultadoAtributoComparado = arg0.getMatDescricao().compareTo(arg1.getMatDescricao());  
	                return resultadoAtributoComparado; 	
				}
			});
			
			return result;
		}
		else{
			throw new ApplicationBusinessException(ConsultarExamesPolONExceptionCode.PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO);
		}
	}
	
	public Boolean verificarExibicaoNodoDadoHistorico(Integer codPaciente) {
		/*if(!getPacienteFacade().verificarAipPacienteHist(codPaciente)){
			return Boolean.FALSE;
		}else */if(getExamesLaudosFacade().verificarSePacientePossuiDadoHistoricoPOLAelAtdDiverso(codPaciente, DominioSituacaoItemSolicitacaoExame.LI)){
			return Boolean.TRUE;
		}else if(getExamesLaudosFacade().verificarSePacientePossuiDadoHistoricoPOLAghAtendimento(codPaciente, DominioSituacaoItemSolicitacaoExame.LI)){
			return Boolean.TRUE;
		}else if(getExamesLaudosFacade().verificarSePacientePossuiDadoHistoricoPOLAipMdtoHist(codPaciente, DominioSituacaoItemSolicitacaoExame.LI)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/*private IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}*/
	
	private IExamesLaudosFacade getExamesLaudosFacade(){
		return examesLaudosFacade;
	}
}