package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ItemProcedimentoHospitalarON extends BaseBusiness {
	
	@EJB
	private ItensProcedHospitalarRN itensProcedHospitalarRN;
	
	private static final Log LOG = LogFactory.getLog(ItemProcedimentoHospitalarON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

	private static final long serialVersionUID = 3381602633581390722L;

	/**
	 * Metodo para clonar uma entidade da classe FatItensProcedHospitalar
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatItensProcedHospitalar clonarItemProcedimentoHospitalar(FatItensProcedHospitalar iph) throws Exception {
		if (iph == null) {
			return null;
		}
		FatItensProcedHospitalar cloneIph = (FatItensProcedHospitalar) BeanUtils.cloneBean(iph);

		if (iph.getProcedimentoHospitalar() != null) {
			FatProcedimentosHospitalares procedimentoHospitalar = new FatProcedimentosHospitalares();
			procedimentoHospitalar.setSeq(iph.getProcedimentoHospitalar().getSeq());
			cloneIph.setProcedimentoHospitalar(procedimentoHospitalar);
		}

		if (iph.getClinica() != null) {
			AghClinicas clinica = new AghClinicas();
			clinica.setCodigo(iph.getClinica().getCodigo());
			cloneIph.setClinica(clinica);
		}

		cloneIph.setCaracteristicasItemProcHosp(iph.getCaracteristicasItemProcHosp());
		cloneIph.setGruposItensProced(iph.getGruposItensProced());
		cloneIph.setInternacoes(iph.getInternacoes());
		cloneIph.setValoresItemProcdHospComps(iph.getValoresItemProcdHospComps());

		if (iph.getFatCaracteristicaFinanciamento() != null) {
			FatCaractFinanciamento fatCaracteristicaFinanciamento = new FatCaractFinanciamento();
			fatCaracteristicaFinanciamento.setSeq(iph.getFatCaracteristicaFinanciamento().getSeq());
			cloneIph.setFatCaracteristicaFinanciamento(fatCaracteristicaFinanciamento);
		}

		cloneIph.setEspelhosAihSolicitado(iph.getEspelhosAihSolicitado());

		cloneIph.setEspelhosAihRealizado(iph.getEspelhosAihRealizado());

		return cloneIph;
	}

	/**
	 * Metodo para persistir um itemProcedimentoHospitalar. Se a operação for um
	 * insert, passar o segundo parâmetro nulo.
	 */
	public void persistirItemProcedimentoHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException {

		if (newIph.getId() == null || (newIph.getId() != null && newIph.getId().getSeq() == null)) {
			this.inserirItemProcedimentoHospitalar(newIph);
		} else {
			this.atualizarItemProcedimentoHospitalar(newIph, oldIph);
		}
	}

	/**
	 * Metodo para persistir um itemProcedimentoHospitalar, realizando flush ao
	 * final Se a operação for um insert, passar o segundo parâmetro nulo.
	 */
	public void persistirItemProcedimentoHospitalarComFlush(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException {

		this.persistirItemProcedimentoHospitalar(newIph, oldIph);

		this.getFatItensProcedHospitalarDAO().flush();
	}

	/**
	 * Metodo para inserir um itemProcedimentoHospitalar.
	 */
	public void inserirItemProcedimentoHospitalar(FatItensProcedHospitalar newIph) {
		itensProcedHospitalarRN.executarAntesDeInserirItemProcedHospitalar(newIph);
		fatItensProcedHospitalarDAO.persistir(newIph);
		itensProcedHospitalarRN.executarStatementAposInserirItemProcedHospitalar(newIph);
	}

	/**
	 * Metodo para atualizar um itemProcedimentoHospitalar.
	 */
	public void atualizarItemProcedimentoHospitalar(FatItensProcedHospitalar newIph, FatItensProcedHospitalar oldIph) throws BaseException {
		itensProcedHospitalarRN.executarAntesDeAtualizarItemProcedHospitalar(newIph);
		this.getFatItensProcedHospitalarDAO().merge(newIph);
		itensProcedHospitalarRN.executarStatementAposAtualizarItemProcedHospitalar(newIph, oldIph);
		itensProcedHospitalarRN.executarAposAtualizarItemProcedHospitalar(newIph, oldIph);
	}

	
	/**
	 * 41082 - alteração da visibilidade
	 */
	
	public void atualizarItemProcedimentoHospitalarTransplante(FatItensProcedHospitalar novo, FatItensProcedHospitalar antigo) throws BaseException {		
		ItensProcedHospitalarRN itensProcedHospitalarRN = this.getItensProcedHospitalarRN();

		itensProcedHospitalarRN.executarAntesDeAtualizarItemProcedHospitalar(novo);
		this.getFatItensProcedHospitalarDAO().atualizar(novo);
		itensProcedHospitalarRN.executarStatementAposAtualizarItemProcedHospitalar(novo, antigo);
		itensProcedHospitalarRN.executarAposAtualizarItemProcedHospitalar(novo, antigo);
	}
	
	/*
	public void executarAntesAtualizarFatItensProcedHospitalarCustom(final FatItensProcedHospitalar newIph){
		 this.getItensProcedHospitalarRN().executarAntesDeAtualizarItemProcedHospitalar(newIph);
	}

	public void executarAposAtualizarFatItensProcedHospitalarCustom(final FatItensProcedHospitalar newIph, final FatItensProcedHospitalar oldIph){
		ItensProcedHospitalarRN itensProcedHospitalarRN = this.getItensProcedHospitalarRN();
		itensProcedHospitalarRN.executarStatementAposAtualizarItemProcedHospitalar(newIph, oldIph);
		itensProcedHospitalarRN.executarAposAtualizarItemProcedHospitalar(newIph, oldIph);
	}*/
	/**
	 * Metodo para remover um itemProcedimentoHospitalar.
	 * 
	 * @param FatItensProcedHospitalar
	 *  
	 */
	@SuppressWarnings("ucd")
	public void removerItemProcedimentoHospitalar(FatItensProcedHospitalar iph) throws BaseException {
		ItensProcedHospitalarRN itensProcedHospitalarRN = this.getItensProcedHospitalarRN();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = this.getFatItensProcedHospitalarDAO();

		fatItensProcedHospitalarDAO.remover(iph);

		itensProcedHospitalarRN.executarAposDeletarItemProcedHospitalar(iph);

		fatItensProcedHospitalarDAO.flush();

	}

	private ItensProcedHospitalarRN getItensProcedHospitalarRN() {
		return itensProcedHospitalarRN;
	}

	private FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
}
