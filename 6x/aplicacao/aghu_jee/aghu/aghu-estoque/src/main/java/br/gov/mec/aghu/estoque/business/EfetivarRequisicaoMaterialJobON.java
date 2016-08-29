package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.mail.EmailUtil;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #5621 - Efetivar uma requisição de material
 * @author aghu
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class EfetivarRequisicaoMaterialJobON extends BaseBMTBusiness {

	@EJB
	private SceItemRmsRN sceItemRmsRN;
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	@EJB
	private SceReqMateriaisRN sceReqMateriaisRN;
	
	private static final Log LOG = LogFactory.getLog(EfetivarRequisicaoMaterialJobON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemRmsDAO sceItemRmsDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8809121407754224776L;

	public void efetivarRequisicaoMaterialAutomatica(String nomeMicrocomputador) throws ApplicationBusinessException {
		LOG.info("Rotina EfetivarRequisicaoMaterialON.efetivarRequisicaoMaterialAutomatica iniciada em: "+ Calendar.getInstance().getTime());

		List<SceReqMaterial> requisicoesMaterial = this.getSceReqMateriaisDAO().buscaRequisicaoMaterialEfetivacaoAutomatica();
		StringBuilder mensagemDeErro = new StringBuilder();
		
		for (SceReqMaterial requisicao : requisicoesMaterial){
			this.beginTransaction();
			
			try {
				processarRequisicaoMaterialAutomatica(requisicao, nomeMicrocomputador);
				this.commitTransaction();
			} catch (BaseException e) {
				String msgErro = "Erro ao processar requisição de material com ID "+ requisicao.getSeq() +" : "+ e.getMessage();
				LOG.error(msgErro, e);
				mensagemDeErro.append(msgErro);
				mensagemDeErro.append("<BR/>");
				this.rollbackTransaction();
			} catch (Exception e) {
				String msgErro = "Erro ao processar requisição de material com ID "+ requisicao.getSeq() +" : "+ Arrays.toString(e.getStackTrace());
				LOG.error(msgErro, e);
				mensagemDeErro.append(msgErro);
				mensagemDeErro.append("<BR/>");
				this.rollbackTransaction();
			}
		}
		
		//se houver erro envia e-mail
		if (mensagemDeErro !=null && mensagemDeErro.length()>1){
			AghParametros emailDe = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
			
			List<String> emailParaList = new ArrayList<String>();
			String emailParaParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_RM_AUTOMATICA).getVlrTexto();
			if (emailParaParametro == null || emailParaParametro.length()==0){
				LOG.info("Parametro P_AGHU_EMAIL_RM_AUTOMATICA está nulo. Não foi possivel enviar e-mail de erro.");
			}else{
				StringTokenizer emailPara = new StringTokenizer(emailParaParametro,";");
				while (emailPara.hasMoreTokens()) {
					emailParaList.add(emailPara.nextToken().trim().toLowerCase());
				}			
				this.getEmailUtil().enviaEmail(emailDe.getVlrTexto(), emailParaList, null, "Ocorreu erro ao efetivar requisição de material automática", mensagemDeErro.toString());
			}
		}	

		LOG.info("Rotina EfetivarRequisicaoMaterialON.efetivarRequisicaoMaterialAutomatica finalizada em: "+ Calendar.getInstance().getTime());
	}

	private void processarRequisicaoMaterialAutomatica(SceReqMaterial requisicao, String nomeMicrocomputador) throws BaseException {
		requisicao = this.getSceReqMateriaisDAO().merge(requisicao);//faz a atualização apenas para recuperar a referencia do objeto.
		List<SceItemRms> itensRequisicaoMaterial = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(requisicao.getSeq());
		if (itensRequisicaoMaterial == null || itensRequisicaoMaterial.isEmpty()){
			LOG.info("Excluindo requisicao sem itens. ID: "+ requisicao.getSeq());
			this.getSceReqMateriaisDAO().remover(requisicao);
		}else{
			requisicao.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
			this.getSceReqMateriaisRN().atualizar(requisicao, nomeMicrocomputador);
			SceEstoqueAlmoxarifadoDAO estoqueAlmoxarifadoDAO = getSceEstoqueAlmoxarifadoDAO();
			for (SceItemRms item : itensRequisicaoMaterial){
				SceEstoqueAlmoxarifado estoqueAlmoxarifado = 
						estoqueAlmoxarifadoDAO.obterPorChavePrimaria(item.getEstoqueAlmoxarifado().getSeq());
				Integer qtdeBloqDispensacao = estoqueAlmoxarifado.getQtdeBloqDispensacao();
				Integer qtdeRequisitada = item.getQtdeRequisitada();
				LOG.info("Atualizando estoque almoxarifado de requisicao. ID: " + estoqueAlmoxarifado.getSeq());
				LOG.info("Qtd requisitada na RM: " + qtdeRequisitada);
				LOG.info("Qtd disponivel antes de atualizar: "+estoqueAlmoxarifado.getQtdeDisponivel());
				LOG.info("Qtd bloqueado antes de atualizar: " + qtdeBloqDispensacao);
				if (qtdeBloqDispensacao != null) {
					qtdeBloqDispensacao -= qtdeRequisitada;
				} else {
					qtdeBloqDispensacao = 0;
				}
				estoqueAlmoxarifado.setQtdeBloqDispensacao(qtdeBloqDispensacao);
				Integer totalDisponivel = estoqueAlmoxarifado.getQtdeDisponivel() + qtdeRequisitada;
				estoqueAlmoxarifado.setQtdeDisponivel(totalDisponivel);
				getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
				LOG.info("Qtd disponivel após atualizar: "+estoqueAlmoxarifado.getQtdeDisponivel());
			}
			requisicao.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
			LOG.info("Atualizando requisicao. ID: " + requisicao.getSeq());
			this.getSceReqMateriaisRN().atualizar(requisicao, nomeMicrocomputador);
		}
	}
	
	/**
	 * get de RNs e DAOs
	 */
	protected SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}
	
	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}
	
	protected SceItemRmsRN getSceItemRmsRN() {
		return sceItemRmsRN;
	}
	
	protected SceItemRmsDAO getSceItemRmsDAO(){
		return sceItemRmsDAO;
	}
	
	protected SceReqMateriaisDAO getSceReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected EmailUtil getEmailUtil() {
		return (EmailUtil) emailUtil;
	}
	
	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
}
