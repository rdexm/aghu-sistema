package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;

@Stateless
public class AltasPorUnidadeRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AltasPorUnidadeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {

		return LOG;

	}

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6557573361939465397L;

	/**
	 * Verifica se uma especialidade tem determinada característica 
	 * ORADB FUNCTION AGHC_VER_CARACT_ESP
	 * 
	 * @author Stanley Araujo
	 * @param seq
	 *            - Código da agh_caract_unid_funcionais
	 * @param caracteristica
	 *            - Característica
	 * 
	 * */
	public Boolean verificarCaracteristicaEspecialidade(Short seq,
			DominioCaracEspecialidade caracteristica) {

		if (seq == null || caracteristica == null) {
			return false;
		} else {
			List<AghCaractEspecialidades> caracteristicaList = this
					.pesquisarCaracteristicaEspecialidade(seq, caracteristica);

			return caracteristicaList.size() > 0;
		}

	}

	/**
	 * Método para fazer busca de especialidade pelo seu seq e sua
	 * característica.
	 * 
	 * @author Stanley Araujo
	 * @param seq
	 *            - Código da
	 * @param caracteristica
	 * @return lista de unidades funcionais
	 */
	private List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(
			Short seq, DominioCaracEspecialidade caracteristica) {
		return getAghuFacade().pesquisarCaracteristicaEspecialidade(seq, caracteristica);
	}
	
		
	/**
	 * ORADB function   LOCAL_ORIGEM
	 * @author Stanley Araujo
	 * 
	 * @param seq
	 *            - Código da internação
	 * @param data
	 *            - Data de lancamento
	 * */
	
	public String obterLocalOrigem(Integer seq, Date data){
		String ret = "";
		String[] retorno = this.obterOrigem(seq, data);
		if (retorno[0] != null) {
			ret = retorno[2];
		}
		return ret;
	}
	
	/**
	 * 
	 * ORADB function  UNF_SEQ_ORIGEM
	 * 
	 * @author Stanley Araujo
	 * @param seq
	 *            - Código da internação
	 * @param data
	 *            - Data de lancamento
	 * 
	 * */
	public Short obterUnfSeqOrigem(Integer seq, Date data) {
		Short ret = 0;
		String[] retorno = this.obterOrigem(seq, data);
		if(retorno[0]!= null) {
			ret = Short.valueOf(retorno[0]);
		}
		return ret;
	}

	/**
	 * 
	 * ORADB CURSOR origem
	 * 
	 * @author Stanley Araujo
	 * @param seq
	 *            - Código da internação
	 * @param data
	 *            - Data de lancamento
	 * @return Um array contendo os retornos
	 *       retorno0 - Código da unidade funcional
	 *       retorno1 - Data de lançamento
	 *       retorno2 - Identificador do leito
	 *       retorno3 -    Código da especialidade
	 * */
	private String[] obterOrigem(Integer seq, Date data) {

		Object[] res = (Object[]) getAinMovimentoInternacaoDAO().obterOrigem(seq, data);
		AghUnidadesFuncionais unidadesFuncionais = null;
		Date dataLancamento = null;
		SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
		AinLeitos leito = null;
		AinQuartos quarto = null;
		AghEspecialidades especialidade = null;
		
		String retorno0=null;
		String retorno1=null;
		String retorno2=null;
		String retorno3=null;
		
		
		if (res != null) {

			unidadesFuncionais = (AghUnidadesFuncionais) res[0];
			dataLancamento = (Date) res[1];
			leito = (AinLeitos) res[2];
			quarto = (AinQuartos) res[3];
			especialidade = (AghEspecialidades) res[4];
		}
		if(unidadesFuncionais!=null){
			retorno0 = String.valueOf( unidadesFuncionais.getSeq());
		}
		if(dataLancamento!=null){
			
			retorno1 = format.format(dataLancamento);
		}
		
		
		if (leito == null || leito.getLeitoID() == null) {
			
			if (quarto == null || quarto.getNumero() == null) {
				String andar = StringUtils.EMPTY;
				AghAla ala = null;
				if(unidadesFuncionais!=null){
				   andar = unidadesFuncionais.getAndar();
				    ala = unidadesFuncionais.getIndAla();
				}
				retorno2 = andar + " " + ala;
			}else{
				String quartoNumero="";
				 quartoNumero = String.valueOf(quarto.getNumero());
				
				if(quartoNumero.length()>3) {
					quartoNumero = quartoNumero.substring(0,4)+ " ";
				}
				
				retorno2 = quartoNumero;
			}
			
			
		}else{
			retorno2= leito.getLeitoID();
		}	
		
		if(especialidade!=null){
			retorno3 = String.valueOf(especialidade.getSeq());
		}

		return new String[]{retorno0,retorno1,retorno2,retorno3};
		
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
