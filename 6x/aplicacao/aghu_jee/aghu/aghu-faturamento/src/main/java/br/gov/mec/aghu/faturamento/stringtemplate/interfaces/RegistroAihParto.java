package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioGrauInstru;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStGestrisco;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTpContracep;

public interface RegistroAihParto
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		QT_VIVOS("qt_vivos"),
		QT_MORTOS("qt_mortos"),
		QT_ALTA("qt_alta"),
		QT_TRANSF("qt_transf"),
		QT_OBITO("qt_obito"),
		QT_FILHOS("qt_filhos"),
		GRAU_INSTRU("grau_instru"),
		CID_INDICACAO("cid_indicacao"),
		TP_CONTRACEP1("tp_contracep1"),
		TP_CONTRACEP2("tp_contracep2"),
		ST_GESTRISCO("st_gestrisco"),
		NU_PRENATAL("nu_prenatal"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Byte getQtVivos();

	public abstract Byte getQtMortos();

	public abstract Byte getQtAlta();

	public abstract Byte getQtTransf();

	public abstract Byte getQtObito();

	public abstract Byte getQtFilhos();

	public abstract DominioGrauInstru getGrauInstru();

	public abstract String getCidIndicacao();

	public abstract DominioTpContracep getTpContracep1();

	public abstract DominioTpContracep getTpContracep2();

	public abstract DominioStGestrisco getStGestrisco();

	public abstract Long getNuPrenatal();
}
