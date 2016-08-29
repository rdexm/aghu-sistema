package br.gov.mec.aghu.faturamento.business;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatServicosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimento2DAO;
import br.gov.mec.aghu.faturamento.vo.CursorAtoFatcBuscaServClassVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaCnesVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaUnidadeVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatcBuscaServClassRN extends BaseBusiness {


@EJB
private RelatorioResumoCobrancaAihRN relatorioResumoCobrancaAihRN;

private static final Log LOG = LogFactory.getLog(FatcBuscaServClassRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

@Inject
private FatServicosDAO fatServicosDAO;

@Inject
private VFatAssociacaoProcedimento2DAO vFatAssociacaoProcedimento2DAO;
	 
	private static final long serialVersionUID = -8385116999021885147L;

	/**
	 * ORADB: FATC_BUSCA_SERV_CLASS
	 */
	public String fatcBuscaServClass(final Integer cthSeq, Integer iphSeq, Short iphPhoSeq, final Short pUnf) throws ApplicationBusinessException{
		final String vRetorno;
		
		if(cthSeq != null){
			vRetorno = verificaInt(iphSeq, iphPhoSeq, cthSeq);
		} else {
			vRetorno = verificaAmb(iphSeq, iphPhoSeq, cthSeq, pUnf);
		}
		
		return vRetorno;
	}

	private String verificaAmb(Integer iphSeq, Short iphPhoSeq, Integer cthSeq, final Short pUnf) {
		final String vRetorno = buscaCnes(pUnf, cthSeq, iphPhoSeq, iphSeq);
		LOG.debug("Serv/class da unf:" + vRetorno);
		return (String) CoreUtil.nvl(vRetorno, "      ");
	}

	private String verificaInt(Integer iphSeq, Short iphPhoSeq, Integer cthSeq) throws ApplicationBusinessException {
		if(getRelatorioResumoCobrancaAihRN().buscaInstrRegistro(iphSeq, iphPhoSeq, "05")){
			// Só determinar servico/classificação quando forma de registro for diferente de PROC. SECUNDARIO
			return "000000"; 
		} else {
			return verificaCnes(cthSeq, iphSeq, iphPhoSeq);
		}
	}

	private String verificaCnes(Integer cthSeq, Integer iphSeq, Short iphPhoSeq) throws ApplicationBusinessException {
		String vRetorno = null;
		Short vUnf = null;
		CursorBuscaUnidadeVO rUnid = null;
		
		final List<CursorBuscaUnidadeVO> cBuscaUnidades = getVFatAssociacaoProcedimento2DAO().obterCursorCBuscaUnidade(cthSeq, iphSeq, iphPhoSeq);
		
		if(cBuscaUnidades != null && !cBuscaUnidades.isEmpty()){
			rUnid = cBuscaUnidades.get(0);
			vUnf = buscaUnidadeOrigem( DominioIndOrigemItemContaHospitalar.valueOf(rUnid.getIndOrigem()), 
									   rUnid.getUfeUnfSeq(), rUnid.getUnfseq(), rUnid.getIpsRmpSeq());
			
		} else {
			LOG.debug("Não encontrou item conta.");
		}
		
		if(vUnf == null){
			LOG.debug("Busca ato obrigatorio, iph:"+ iphSeq);
			
			
			final Ato ato = verificaAtoObrigatorio( vUnf, iphPhoSeq, iphSeq, cthSeq, 
										    	  Short.valueOf("1"), 
										    	  Byte.valueOf("1"), Byte.valueOf("2") 
										        );
			if(ato.result){
				vUnf = ato.pUnf;
				LOG.debug("Ato obrigatorio encontrado, unf:"+ vUnf);
			}
			
		}
		
		// Não pode ser else, pois vUnf pode ser alterado no if anterior
		if(vUnf != null){
			LOG.debug("Vai buscar serv/class da unf:"+ vUnf);
			vRetorno = buscaCnes(vUnf, cthSeq, iphPhoSeq, iphSeq);
			LOG.debug("Serv/class da unf:"+ vRetorno);
		}
			
		return (String) CoreUtil.nvl(vRetorno, "      ") ;
	}
	
	private String buscaCnes(final Short pUnf, final Integer pCthSeq, Short phoSeq, Integer iphSeq) {
		String vRetorno = null;
		
		if(pUnf != null){
			final List<CursorBuscaCnesVO> cBuscaCnes = getFatServicosDAO().buscarCursorCNES(iphSeq, phoSeq, DominioSituacao.A, pUnf);
			
			if(cBuscaCnes != null && !cBuscaCnes.isEmpty()){
				CursorBuscaCnesVO rCnes = cBuscaCnes.get(0);
				
				if(rCnes.getUnfSeq() != null && rCnes.getUnfSeq() != 0){
					vRetorno = rCnes.getServClass();
					
				// Só para ambulatorio retorna o serv clasificação do procedimento
				} else if(pCthSeq == null){
					vRetorno = (String) CoreUtil.nvl(rCnes.getServClass(), "000000");
				}
			}
		}
		
		// Só para ambulatorio retorna o serv clasificação do procedimento
		if(vRetorno == null && pCthSeq == null){
			vRetorno = "      ";
		}
		
		return vRetorno;
	}

	private class Ato{
		public Ato(boolean result, Short pUnf) {
			super();
			this.result = result;
			this.pUnf = pUnf;
		}
		private boolean result;
		private Short pUnf;
	}
	
	private Ato verificaAtoObrigatorio(Short pUnf, Short iphPhoSeqCobrado, Integer iphSeqCobrado, Integer cthSeq, Short cpgCphCspCnvCodigo, Byte ...cpgCphCspSeq) throws ApplicationBusinessException{
		
		final List<CursorAtoFatcBuscaServClassVO> cAto = getFatItemContaHospitalarDAO().obterCursorAtoFatcBuscaServClass( iphPhoSeqCobrado, 
																													  	  iphSeqCobrado, 
																													  	  cthSeq, 
																													  	  cpgCphCspCnvCodigo,
																													  	  cpgCphCspSeq);
		if(cAto != null && !cAto.isEmpty()){
			CursorAtoFatcBuscaServClassVO rAto = cAto.get(0);
			
			LOG.debug("Ato obrigatorio origem"+rAto.getIndOrigem()+" "+rAto.getCodTabelaAto()+" iph_seq="+rAto.getIphSeqCobrado()+" "+ rAto.getDescricaoAto());
			LOG.debug("Ato obrigatorio "+rAto.getIndOrigem()+" "+rAto.getCodTabelaAto()+" iph_seq="+rAto.getIphSeqCobrado()+" "+ rAto.getDescricaoAto());
			pUnf = buscaUnidadeOrigem( DominioIndOrigemItemContaHospitalar.valueOf(rAto.getIndOrigem()), 
									   rAto.getUfeUnfseq(),rAto.getUnfSeq(), rAto.getIpsRmpSeq()
									 );
			
			return new Ato(true, pUnf);
		}
		
		return new Ato(false, pUnf);
	}
	
	private Short buscaUnidadeOrigem(final DominioIndOrigemItemContaHospitalar indOrigem, final Short ufeUnfSeq, final Short unfSeq, final Integer ipsRmpSeq) throws ApplicationBusinessException{
		final Short retorno;
		
		switch (indOrigem) {
			case AEL: retorno = ufeUnfSeq; break; 
			case ABS: retorno = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_UNID_BANCO_SANGUE); break;
			case AFA: retorno = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_UNF_FARM_DISP); break;
			case FIS: retorno = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_UNIDADE_FISIATRIA); break;
			case ANU: retorno = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_UNIDADE_NUTRICAO); break;
			case BCC: 
					if(ipsRmpSeq == null){
						retorno = unfSeq;
					} else {
						retorno = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_COD_UNIDADE_ALMOXARIFADO); break;
					} 
				break;
			case DIG: retorno = unfSeq; break;
			
			default: retorno = null; break;
		}
		
		return retorno;
	}
	
	/**
	 * ORADB: AGHC_REM_CARAC_ESP
	 * @param valor
	 * @param tipo
	 * @param excecao
	 * @return
	 */
	public String aghcRemoveCaracterEspecial(String valor, String tipo, char excecao) {
		String wTipo=null;
		// Substitui o caracter por branco
		if(String.valueOf('B').equalsIgnoreCase(tipo)){
			wTipo = String.valueOf(' ');
		}
		
		// retira o caracter
		if(String.valueOf('R').equalsIgnoreCase(tipo)){
			wTipo = null;
		}
		
		// Exceção, quando for para trocar por um espaço em branco, os caracteres abaixo
		// não devem ser REMOVIDOS no regex que retira os ascentos
		if(tipo != null){
			valor = executa(valor, excecao, '¨', wTipo);
			valor = executa(valor, excecao, '´', wTipo);
		}
		
		// altera: ãõáéíóúçäëïöüâêîôûàèìòùçÃÕÁÉÍÓÚÇÄËÏÖÜÂÊÎÔÛÀÈÌÒÙÇ para
		//         aoaeioucaeiouaeiouaeioucAOAEIOUCAEIOUAEIOUAEIOUC
		String aux = Normalizer.normalize(valor, Normalizer.Form.NFD);
		valor = aux.replaceAll("[^\\p{ASCII}]", "");

		
		char[] caracteresInvalidos = {'@','"','#','$','%','¨','&','*','(',')','-','_','+','=','`','´','{','[','^','~','}',']',':',';',
									  '?','/','<','>','.', ',', '!','|','\\', 'ª','º', '°', '§', '¹', '²', '³', '£', '¢', '¬', '\''};

		for (char c : caracteresInvalidos) {
			valor = executa(valor, excecao, c, wTipo);
		}

		return valor.trim();
	}
	
	private static String executa(String result, char excecao, char charToRemove, String newChar){
		if(excecao != charToRemove){
			// Para os caracteres abaixo o replace e remove não estavam funcionando, tornei-os como exceções
			if(charToRemove == '|' || charToRemove == '.'|| charToRemove == '^' || charToRemove == '$'){

				String caracteresInvalidos = "|.^$";
				String retorno = "";
				
				if(newChar != null){
					// Altera o caracter especial para newChar
					for (int i = 0; i < result.length(); i++) {
						Character c = result.charAt(i);
						if (caracteresInvalidos.indexOf(c.toString()) > -1) {
							retorno = retorno + newChar; //NOPMD soma apenas duas strings.
						} else {
							retorno = retorno + c.toString(); //NOPMD soma apenas duas strings.
						}
					}
					
				} else {
					// Remove o caracter especial
					for (int i = 0; i < result.length(); i++) {
						Character c = result.charAt(i);
						if (caracteresInvalidos.indexOf(c.toString()) > -1) {
							continue;
						} else {
							retorno = retorno + c.toString(); //NOPMD soma apenas duas strings.
						}
					}
				}
				
				return retorno;
			
			// Para todos os outros caracteres inválidos, entrará aqui
			} else {
			
				try{
					if(newChar != null){
						return result.replaceAll(String.valueOf(charToRemove), newChar);
					} else {
						return StringUtils.remove(result, charToRemove);
					}
				} catch (PatternSyntaxException e) {
					if(newChar != null){
						return result.replaceAll("\\"+charToRemove, newChar);
					} else {
						return StringUtils.remove("\\"+charToRemove, charToRemove);
					}
				}
			}
		} else {
			return result;
		}
	}
	
	private RelatorioResumoCobrancaAihRN getRelatorioResumoCobrancaAihRN(){
		return relatorioResumoCobrancaAihRN;
	}
	
	private VFatAssociacaoProcedimento2DAO getVFatAssociacaoProcedimento2DAO() {
		return vFatAssociacaoProcedimento2DAO;
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}
	
	private FatServicosDAO getFatServicosDAO(){
		return fatServicosDAO;
	}
}
