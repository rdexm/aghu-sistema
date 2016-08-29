package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaOrtProteseDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaOrtProteseId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendaOrtProtese.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendaOrtProteseRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaOrtProteseRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaOrtProteseDAO mbcAgendaOrtProteseDAO;


	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;
	private static final long serialVersionUID = 6962249552758027250L;
	
	public enum MbcAgendaOrtProteseRNExceptionCode implements BusinessExceptionCode {
		MBC_00835;
	}

	public void persistirAgendaOrtProtese(MbcAgendaOrtProtese agendaOrtProtese) throws ApplicationBusinessException {
		if (agendaOrtProtese.getId() == null) {
			definirId(agendaOrtProtese);
			this.getMbcAgendaOrtProteseDAO().persistir(agendaOrtProtese);
		} else {
			this.getMbcAgendaOrtProteseDAO().merge(agendaOrtProtese);
		}
	}

	private void definirId(MbcAgendaOrtProtese agendaOrtProtese){
		MbcAgendaOrtProteseId id = new MbcAgendaOrtProteseId();
		id.setAgdSeq(agendaOrtProtese.getMbcAgendas().getSeq());
		id.setMatCodigo(agendaOrtProtese.getScoMaterial().getCodigo());

		agendaOrtProtese.setId(id);
	}
	
	/**
	 * remover ortese protese da agenda
	 * 
	 * @param agendaOrtProteseRemoveList
	 * @param usuarioLogado
	 * @throws BaseException
	 */
	public void removerAgendasOrteseProtese(MbcAgendaOrtProtese mbcAgendaOrtProtese) throws BaseException{
	 	preDeletar(mbcAgendaOrtProtese);
	   	getMbcAgendaOrtProteseDAO().remover(mbcAgendaOrtProtese);
	}
	
	/**
	 * Inserir ortese Protese da Agenda
	 * 
	 * @param agendaOrtProteseInserirList
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirAgendasOrteseProtese(MbcAgendaOrtProtese agendaOrtProtese) throws BaseException{
	  	preInserir(agendaOrtProtese);
	   	persistirAgendaOrtProtese(agendaOrtProtese);
	}
	
	/**
	 * Atualizar ortese protese da agenda
	 * 
	 * @param agendaOrtProteseAlterarList
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void alterarAgendasOrteseProtese(MbcAgendaOrtProtese agendaOrtProteseOld, MbcAgendaOrtProtese agendaOrtProtese) throws BaseException{
		preAtualizar(agendaOrtProteseOld, agendaOrtProtese);
		persistirAgendaOrtProtese(agendaOrtProtese);
	}
	
	/**
	 * @ORADB MBCT_AGO_BRI
	 * 
	 * RN1: Seta campo CRIADO_EM para a data corrente do sistema. (new Date());
	 * RN2: Seta as colunas ser_matricula e ser_vin_codigo de acordo com o servidor logado.
	 * RN3: Chama mbck_ago_rn.rn_agop_ver_escala 
	 * 
	 */
	public void preInserir(MbcAgendaOrtProtese agendaOrtProtese) throws BaseException  {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		agendaOrtProtese.setCriadoEm(new Date());
		agendaOrtProtese.setRapServidores(servidorLogado);
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaOrtProtese.getMbcAgendas());
		
	}
	
	/**
	 * @ORADB MBCT_AGO_BRU
	 *  
	 * RN1: Seta as colunas ser_matricula e ser_vin_codigo de acordo com o servidor logado.
	 * RN2: Chama mbck_ago_rn.rn_agop_ver_escala
	 * RN4: Se o material ou a quantidade da órtese/prótese foram modificados, chama mbck_ago_rn.rn_agop_inc_historic para UPDATE 
	 */
	public void preAtualizar(MbcAgendaOrtProtese agendaOrtProteseOriginal,MbcAgendaOrtProtese agendaOrtProtese)throws BaseException  {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		agendaOrtProtese.setRapServidores(servidorLogado);
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaOrtProtese.getMbcAgendas());
		if (CoreUtil.modificados(agendaOrtProtese.getScoMaterial(),agendaOrtProteseOriginal.getScoMaterial())
				|| CoreUtil.modificados(agendaOrtProtese.getQtde(), agendaOrtProteseOriginal.getQtde())){
			popularSalvarHistoricoAgenda(agendaOrtProtese,agendaOrtProteseOriginal,agendaOrtProtese.getRapServidores());
		}
	}
	
	/**
	 * @ORABD MBCT_AGO_BRD
	 * 
	 * RN1: Chama mbck_ago_rn.rn_agop_ver_escala
	 * RN2: Chama mbck_ago_rn.rn_agop_inc_historic para DELETE
	 */
	public void preDeletar(MbcAgendaOrtProtese agendaOrtProtese)throws BaseException  {
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaOrtProtese.getMbcAgendas());
		popularSalvarHistoricoAgenda(agendaOrtProtese,null,agendaOrtProtese.getRapServidores());
	}
	
	
	/**
	 * @ORADB mbck_ago_rn.rn_agop_inc_historic 
	 * @ORADB mbck_ahi_rn.rn_ahip_inclui
	 * 
	 * @param newMbcAgendaOrtProtese
	 * @param oldMbcAgendaOrtProtese
	 * @throws BaseException 
	 */
	public void popularSalvarHistoricoAgenda(MbcAgendaOrtProtese newMbcAgendaOrtProtese, MbcAgendaOrtProtese oldMbcAgendaOrtProtese, RapServidores servidor) throws BaseException {
		DominioOperacaoAgenda operacaoHistorico;
		StringBuffer descricao = new StringBuffer();
		if(oldMbcAgendaOrtProtese != null) {
			String material = "";
			String qtde = "";
			operacaoHistorico = DominioOperacaoAgenda.A;
			if (CoreUtil.modificados(newMbcAgendaOrtProtese.getScoMaterial(),oldMbcAgendaOrtProtese.getScoMaterial())){
				material ="Material Ortese/Prótese alterado de " + oldMbcAgendaOrtProtese.getScoMaterial().getNome()
					+ " para " + newMbcAgendaOrtProtese.getScoMaterial().getDescricao();
			}else{
				material ="Material Ortese/Prótese: "+oldMbcAgendaOrtProtese.getScoMaterial().getNome()+", ";
			}
			if (CoreUtil.modificados(newMbcAgendaOrtProtese.getQtde(),oldMbcAgendaOrtProtese.getQtde())){
				qtde = "Quantidade alterada de " + getOrd(oldMbcAgendaOrtProtese.getQtde())
				+ " para " + getOrd(newMbcAgendaOrtProtese.getQtde());
			}
			descricao.append(material).append(qtde);
		} else {
			operacaoHistorico = DominioOperacaoAgenda.E;
			descricao.append("Material Ortese/Prótese ").append(newMbcAgendaOrtProtese.getScoMaterial().getNome())
				.append(getOrd(newMbcAgendaOrtProtese.getQtde())).append(" excluída");
		}
		getMbcAgendaHistoricoRN().inserir(newMbcAgendaOrtProtese.getMbcAgendas().getSeq(), newMbcAgendaOrtProtese.getMbcAgendas().getIndSituacao(),
				DominioOrigem.O, descricao.toString(), operacaoHistorico);
	}
	
	
	private String getOrd(Short qtde){
		String ord=" unidade";
		if(qtde>1){
			ord = " unidades ";
		}
		return " " + qtde.toString() + ord;
	}
	
	/**
	 * @ORADB mbck_ago_rn.rn_agop_ver_escala
	 * @throws ApplicationBusinessException 
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(MbcAgendas agenda) throws ApplicationBusinessException {
		MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agenda.getSeq());
		if(result != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaOrtProteseRNExceptionCode.MBC_00835);
		}
	}
	
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
	
	protected MbcAgendaOrtProteseDAO getMbcAgendaOrtProteseDAO() {
		return mbcAgendaOrtProteseDAO;
	}
	
}