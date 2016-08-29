package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class MamNotasAnaEvoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MamNotasAnaEvoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 1337895853773952946L;

	/**
	 * @ORADB MAMC_GET_RESP_NOTA
	 * @return
	 * @author bruno.mourao
	 * @param seqAna 
	 * @param seqEvo 
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 16/05/2012
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String obterResponsavelNota(Integer seqAna, Integer seqEvo) throws ApplicationBusinessException{
		StringBuffer retorno = new StringBuffer();
		
		String usaAssUnificada = "N";
		
		//Obter parametro
		AghParametros parametro = obterParamFacade().buscarAghParametro(AghuParametrosEnum.P_USA_ASS_UNIFICADA);
		
		if(parametro != null){
			usaAssUnificada = parametro.getVlrTexto();
		}
		
		
		MamNotaAdicionalAnamneses notaAna = null;
		MamNotaAdicionalEvolucoes notaEvo = null;
		
		if(seqAna != null){
			notaAna = getAmbulatorioFacade().obterNotaAdicionalAnamnesePorChavePrimaria(seqAna);
		}
		if(seqEvo != null){
			
			notaEvo = getAmbulatorioFacade().obterNotaAdicionalEvolucoesPorChavePrimaria(seqEvo);
		}
		if(usaAssUnificada.equals("S")){
			if(seqAna != null){
				retorno.append(getAmbulatorioFacade().obterAssinaturaTexto(null, null, notaAna, null));
			}
			
			if(seqEvo != null){
				retorno.append(getAmbulatorioFacade().obterAssinaturaTexto(null, null, null, notaEvo));
			}
		}
		else{
			RapServidores servidor = null;
			Date dthrValida = null;
			
			if(seqAna != null && notaAna != null){
				if(notaAna.getServidor() != null){
					servidor = notaAna.getServidor();
				}
				
				dthrValida = notaAna.getDthrValida();
			}
			else if(notaEvo != null){
				if(notaEvo.getServidor() != null){
					servidor = notaEvo.getServidor();
				}
				
				dthrValida = notaEvo.getDthrValida();
			}
			
			Object[] profElab = null;
			/*
			 * --
			   -- busca nome profissional que elaborou
			   --
			 */
			if(servidor != null){
				profElab = getPrescricaoMedicaFacade().buscaConsProf(servidor);
			}
			
			if(profElab != null && profElab[1] != null){//nome
				profElab[1] = "Elaborado por " + StringUtils.capitalize(profElab[1].toString());
			}
			
			/*
			 * --
			   -- busca nome profissional que validou
			   --
			 */
			Object[] profValid = null;
			if(servidor != null){
				profValid = getPrescricaoMedicaFacade().buscaConsProf(servidor);
			}
			
			if(profValid != null && profValid[1] != null){
				profValid[1] = " - Assinado por " + StringUtils.capitalize(profValid[1].toString()) + " em " + DateUtil.obterDataFormatada(dthrValida, "dd/MM/yy HH:mm");
			}
			
			retorno.append(profElab[1].toString() + profValid[1].toString());
		}
		
		return retorno.toString();
	}
	
	/**
	 * tipo = 1 - Relatórios<br>
   	 * tipo = 2 - Telas
	 * @ORADB MAMC_EMG_VIS_NAA
	 * @return
	 * @author bruno.mourao
	 * @param numRegistro 
	 * @param tipo 
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 16/05/2012
	 *  
	 */
	public String visualizarNotaAnamneseEMG(Long numRegistro, Integer tipo) throws ApplicationBusinessException{
		StringBuffer retorno = new StringBuffer();
		
		Boolean primeiraVez = true;
		
		//Obter as notas
		//Obtem a primeira parte do union
		List<MamNotaAdicionalAnamneses> lista = getAmbulatorioFacade().pesquisarNotaAdicionalAnamnesesPendenteExcNaoValidParaInternacao(numRegistro);
		//Obtem a segunda parte
		lista.addAll(getAmbulatorioFacade().pesquisarNotaAdicionalAnamnesesValidasSemPaiParaInternacao(numRegistro));
		//Ordena pelo num seq da nota
		Collections.sort(lista, new Comparator<MamNotaAdicionalAnamneses>() {

			@Override
			public int compare(MamNotaAdicionalAnamneses arg0,
					MamNotaAdicionalAnamneses arg1) {
				return arg0.getSeq().compareTo(arg1.getSeq());
			}
		});
		
		
		for(MamNotaAdicionalAnamneses nota : lista){
			if(primeiraVez){
//				-- se foi chamado do relatório não coloca título
				if(tipo != null && tipo != 1){//Tela
					primeiraVez = false;
					retorno.append("\n Nota(s) adicional(is): ");
				}
			}
			
			if((tipo != null && tipo == 1) && primeiraVez) { // chamado do relatório
				primeiraVez = false;
				retorno.append("\n \n ").append(nota.getDescricao());
			}
			else{
				retorno.append("\n---------------------------------------------\n").append(nota.getDescricao());
			}
			
			String resp = obterResponsavelNota(nota.getSeq(), null);
			retorno.append("\n \n").append(resp).append('\n');
		}
		
		/*
		 *  -- se foi chamado do relatório
   			-- tira linhas e espaços em branco do início
		 */
		if(tipo != null && tipo == 1){
			if(retorno != null){
				retorno = new StringBuffer(StringUtil.leftTrim(retorno.toString()));
			}
			while(retorno.toString().startsWith("\n")){
				String aux = retorno.substring(2);
				retorno = new StringBuffer(aux);
			}
		}
		
		return retorno.toString();
	}
	
	/**
	 * tipo = 1 - Relatórios<br>
   	 * tipo = 2 - Telas
	 * @ORADB MAMC_EMG_VIS_NEV
	 * @return
	 * @author bruno.mourao
	 * @param numRegistro 
	 * @param tipo 
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 17/05/2012
	 *  
	 */
	public String visualizarNotaEvolucaoEMG(Long numRegistro, Integer tipo) throws ApplicationBusinessException{
		StringBuffer retorno = new StringBuffer();
		
		Boolean primeiraVez = true;
		
		//Obter as notas
		//Obtem a primeira parte do union
		List<MamNotaAdicionalEvolucoes> lista = getAmbulatorioFacade().pesquisarNotaAdicionalEvolucaoPendenteExcNaoValidParaInternacao(numRegistro);
		//Obtem a segunda parte
		lista.addAll(getAmbulatorioFacade().pesquisarNotaAdicionalEvolucaoValidasSemPaiParaInternacao(numRegistro));
		//Ordena pelo num seq da nota
		Collections.sort(lista, new Comparator<MamNotaAdicionalEvolucoes>() {

			@Override
			public int compare(MamNotaAdicionalEvolucoes arg0,
					MamNotaAdicionalEvolucoes arg1) {
				return arg0.getSeq().compareTo(arg1.getSeq());
			}
		});
		
		
		for(MamNotaAdicionalEvolucoes nota : lista){
			if(primeiraVez){
//				-- se foi chamado do relatório não coloca título
				if(tipo != null && tipo != 1){//Tela
					primeiraVez = false;
					retorno.append("\n Nota(s) adicional(is): ");
				}
			}
			
			if(tipo != null && tipo == 1 && primeiraVez) { // chamado do relatório
				primeiraVez = false;
				retorno.append("\n \n ").append(nota.getDescricao());
			}
			else{
				retorno.append("\n---------------------------------------------\n").append(nota.getDescricao());
			}
			
			String resp = obterResponsavelNota(nota.getSeq(), null);
			retorno.append("\n \n").append(resp).append('\n');
		}
		
		/*
		 *  -- se foi chamado do relatório
   			-- tira linhas e espaços em branco do início
		 */
		if(tipo != null && tipo == 1){
			if(retorno != null){
				retorno = new StringBuffer(StringUtil.leftTrim(retorno.toString()));
			}
			while(retorno.toString().startsWith("\n")){
				String aux = retorno.substring(2);
				retorno = new StringBuffer(aux);
			}
		}
		
		return retorno.toString();
	}
	
	protected IParametroFacade obterParamFacade(){
		return parametroFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}	
	
	

}
