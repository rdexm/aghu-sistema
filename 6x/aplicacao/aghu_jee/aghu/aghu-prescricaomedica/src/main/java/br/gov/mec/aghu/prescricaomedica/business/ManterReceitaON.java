package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamItemReceituarioId;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterReceitaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterReceitaON.class);
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;
	private enum ManterReceitaExceptionCode implements BusinessExceptionCode {
		MSG_MED_OBRIG,MSG_FORM_OBRIG;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	/**
	 * #41635
	 * 
	 */
	public void validarItemReceituario(MamItemReceituario item) throws BaseListException{
		BaseListException listaDeErros = new BaseListException();
		
		if(item.getTipoPrescricao().equals(DominioTipoPrescricaoReceituario.F) && item.getDescricao() == null){
		    listaDeErros.add(new ApplicationBusinessException(ManterReceitaExceptionCode.MSG_MED_OBRIG));
		}else if(item.getTipoPrescricao().equals(DominioTipoPrescricaoReceituario.M) && item.getDescricao()  == null){
			listaDeErros.add(new ApplicationBusinessException(ManterReceitaExceptionCode.MSG_FORM_OBRIG));
		}
		if (listaDeErros.hasException()) {
			throw listaDeErros;
		}
		
	}
	
	public void inserirNovaReceita(MamReceituarios receitaGeral, List<MamItemReceituario> itensReceitaGeral,MamReceituarios receitaEspecial, List<MamItemReceituario> itensReceitaEspecial){
		if(receitaGeral != null){
			inserirNovaReceita(receitaGeral, itensReceitaGeral);
		}
		if(receitaEspecial!=null){
			inserirNovaReceita(receitaEspecial, itensReceitaEspecial);
		}
	}
/**
 * Inserir novaReceita 
 * Obs ao inserir nova receita não é necessario obter seqp. A ordem é igual a seqp porém caso seja necessário incluir ou atualizar um item em um receituario existente
 * sera necessario obter seqp conforme C4
 * @param receita
 * @param itens
 */
	private void inserirNovaReceita(MamReceituarios receita, List<MamItemReceituario> itens) {
		Short seqp=1;Byte ordem=1;
		MamReceituarios receitaMerge;
		if (receita != null) {
			receitaMerge = mamReceituariosDAO.merge(receita);
			receita.setSeq(receitaMerge.getSeq()); //Importante *nao remover
			if(!itens.isEmpty()){
				for (MamItemReceituario item : itens) {
					MamItemReceituarioId id = new MamItemReceituarioId();
					id.setSeqp(seqp);
					id.setRctSeq(receitaMerge.getSeq());
					item.setReceituario(receitaMerge);
					item.setOrdem(ordem);
					item.setId(id);
					item.setIndValidadeProlongada(DominioSimNao.N);
					item.setIndSituacao(DominioSituacao.A);
					mamItemReceituarioDAO.persistir(item);
					seqp++;ordem++;
				}
			}else {
				mamReceituariosDAO.atualizar(receitaMerge);
			}
		}
	}
	/**
	 * Remove receita e os itens associados
	 * @param receitaGeral
	 * @param receitaEspecial
	 */
	public void excluirReceita(MamReceituarios receitaGeral, MamReceituarios receitaEspecial){
		MamReceituarios receitaGeralRetorno=null; MamReceituarios receitaEspecialRetorno=null;
		if(receitaGeral!=null){	
		receitaGeralRetorno = mamReceituariosDAO.obterPorChavePrimaria(receitaGeral.getSeq(), null, new Enum[]{MamReceituarios.Fields.ITEM_RECEITUARIO});
		}
		if(receitaGeralRetorno!=null){
			mamReceituariosDAO.remover(receitaGeralRetorno);
		}
		if(receitaEspecial!=null){
			receitaEspecialRetorno = mamReceituariosDAO.obterPorChavePrimaria(receitaEspecial.getSeq(), null, new Enum[]{MamReceituarios.Fields.ITEM_RECEITUARIO});
		}
		if(receitaEspecialRetorno!=null){
			mamReceituariosDAO.remover(receitaEspecialRetorno);
		}
	}




}
